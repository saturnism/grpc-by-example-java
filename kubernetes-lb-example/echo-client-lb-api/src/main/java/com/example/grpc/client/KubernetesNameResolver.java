package com.example.grpc.client;

import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.internal.SharedResourceHolder;
import io.netty.resolver.InetSocketAddressResolver;

import javax.annotation.concurrent.GuardedBy;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by rayt on 6/22/17.
 */
public class KubernetesNameResolver extends NameResolver {
  private final String namespace;
  private final String name;
  private final int port;
  private final Attributes params;
  private final SharedResourceHolder.Resource<ScheduledExecutorService> timerServiceResource;
  private final SharedResourceHolder.Resource<ExecutorService> sharedChannelExecutorResource;
  private final KubernetesClient kubernetesClient;
  private Listener listener;

  private volatile boolean refreshing = false;
  private volatile boolean watching = false;

  public KubernetesNameResolver(String namespace, String name, int port, Attributes params, SharedResourceHolder.Resource<ScheduledExecutorService> timerServiceResource, SharedResourceHolder.Resource<ExecutorService> sharedChannelExecutorResource) {
    this.namespace = namespace;
    this.name = name;
    this.port = port;
    this.params = params;
    this.timerServiceResource = timerServiceResource;
    this.sharedChannelExecutorResource = sharedChannelExecutorResource;
    this.kubernetesClient = new DefaultKubernetesClient();
  }

  @Override
  public String getServiceAuthority() {
    return kubernetesClient.getMasterUrl().getAuthority();
  }

  @Override
  public void start(Listener listener) {
    this.listener = listener;
    refresh();
  }

  @Override
  public void shutdown() {
    kubernetesClient.close();
  }

  @Override
  @GuardedBy("this")
  public void refresh() {
    if (refreshing) return;
    try {
      refreshing = true;

      Endpoints endpoints = kubernetesClient.endpoints().inNamespace(namespace)
          .withName(name)
          .get();

      if (endpoints == null) {
        // Didn't find anything, retrying
        ScheduledExecutorService timerService = SharedResourceHolder.get(timerServiceResource);
        timerService.schedule(() -> {
          refresh();
        }, 30, TimeUnit.SECONDS);
        return;
      }

      update(endpoints);
      watch();
    } finally {
      refreshing = false;
    }
  }

  private void update(Endpoints endpoints) {
      List<EquivalentAddressGroup> servers = new ArrayList<>();
      endpoints.getSubsets().stream().forEach(subset -> {
        long matchingPorts = subset.getPorts().stream().filter(p -> {
          return p.getPort() == port;
        }).count();
        if (matchingPorts > 0) {
          subset.getAddresses().stream().map(address -> {
            return new EquivalentAddressGroup(new InetSocketAddress(address.getIp(), port));
          }).forEach(address -> {
            servers.add(address);
          });
        }
      });

      listener.onAddresses(servers, Attributes.EMPTY);
  }

  @GuardedBy("this")
  protected void watch() {
    if (watching) return;
    watching = true;

    kubernetesClient.endpoints().inNamespace(namespace)
        .withName(name)
        .watch(new Watcher<Endpoints>() {
          @Override
          public void eventReceived(Action action, Endpoints endpoints) {
            switch (action) {
              case MODIFIED:
              case ADDED:
                update(endpoints);
                return;
              case DELETED:
                listener.onAddresses(Collections.emptyList(), Attributes.EMPTY);
                return;
            }
          }

          @Override
          public void onClose(KubernetesClientException e) {

          }
        });
  }
}
