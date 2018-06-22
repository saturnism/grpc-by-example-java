gRPC Load Balancing Example in Kubernetes
=========================================

L4 Load Balancing
-----------------
Kubernetes can provide L4 load balancing using a ClusterIP service.

gRPC client can connect to the service IP (and with DNS name) directly.
Every time a new connection is opened, it'll be load balanced across
the running server instances.

Deploy the example:
```
$ kubectl apply -f kubernetes/l4-lb/echo-server.yaml
$ kubectl apply -f kubernetes/l4-lb/echo-client.yaml
```

Find the client instances:
```
$ kubectl get pods -l run=echo-client
```

For each instance, see the logs:
```
$ kubectl logs -f echo-client...
```

You may notice that each client instance may be connected to a specific
server instance. This is because the connection is persistent.

When you scale out the server instances, connections are not
automatically rebalanced.

Client-side Load Balancing w/ DNS Service Discovery
---------------------------------------------------
To use gRPC client-side load balancing, you'll need a service discovery
mechanism. For example, Zookeper, Eureka, Consul, etc.

You can write a custom `NameResolver` to lookup a serivce name and find
the endpoints.

You can also use DNS as a service discovery registry by having multiple
`A` records for a single name.

Kubernetes has built-in service discovery. You can discover services
using Kubernetes API, or by using the DNS name. If you use the API,
you'll also need to write a `NameResolver`. If you use DNS name, then
you can use a headless service - this will create a DNS entry with
multiple `A` records, with each `A` record pointing to the IP of a running
instance.

For the DNS client-side load balancing example, use the `DnsNameResolver`,
and for target, use `dns://service-name:port`. Finally, use a client-side
load balancer strategy, such as the `RoundRobinLoadBalancer`.

Deploy the example:
```
$ kubectl apply -f kubernetes/client-side-lb-dns/echo-server.yaml
$ kubectl apply -f kubernetes/client-side-lb-dns/echo-client.yaml
```

Find the client instances:
```
$ kubectl get pods -l run=echo-client
```

For each instance, see the logs:
```
$ kubectl logs -f echo-client...
```

You may notice that each client is now calling different server
instances more evenly.

The entries don't automatically refresh. E.g., if you scale out 
the server instances, the existing clients will not see the new
endpoints. `NameResolver.refresh()` would need to be called
explicitly. On the otherhand, `refresh` will be automatically
called when a connected server shutdown. See [discussion](https://groups.google.com/forum/#!topic/grpc-io/wxgLgjzkR30)


Client-side Load Balancing w/ Kubernetes API Service Discovery
--------------------------------------------------------------
Rather than using the DNS discovery, you can also use Kubernetes API directly to discover
server instance endpoints. Similar to DNS discovery, you can create a headless service.
Then, observe the Kubernete's Endpoints resource:
1. Fetch an initial list
1. Start a watcher to receive endpoints updates

Deploy the example:
```
$ kubectl apply -f kubernetes/client-side-lb-api/echo-server.yaml
$ kubectl apply -f kubernetes/client-side-lb-api/echo-client.yaml
```

Find the client instances:
```
$ kubectl get pods -l run=echo-client
```

For each instance, see the logs:
```
$ kubectl logs -f echo-client...
```

Try to scale the number of `echo-server` instances and observe that the client
is able to connect w/ the new instances quickly:

```
$ kubectl scale deployment echo-server --replicas=4
```

Proxy Load Balancing with Linkerd
---------------------------------
The last example uses Linkerd as a proxy that will load balance the traffic on behalf of the client.
It's possible to run Linkerd as a sidecar for the pod and have the gRPC client connect to its own proxy.
However, all documentation indicates that approach would be inefficient use of resource for Linkerd.

In this example, Linkerd is deployed as a DaemonSet. On each Kubernetes node, it'll expose node port 4140.
gRPC client should connect to its respective node port.

The client will make a request against the URL `/svc/com.example.grpc.EchoService`. In the Linkerd configuration,
under `dtab`, it maps the gRPC URL to `/svc/com.example.grpc.EchoService => /#/io.l5d.k8s/default/grpc/echo-server`.
Which means, to use the `io.l5d.k8s` namer, find the `echo-server` service, for the port named `grpc`, in the `default`
namespace. The `echo-service` is configured as a headless service, because we shouldn't use the L4 load balancer.

Finally, on the client side, it uses Kubernetes Downwards API to fetch the name of the Kubernetes node that the
client is running on, and configure gRPC client to open a connection to the node's port 4140.

Deploy the example:
```
$ kubectl apply -f kubernetes/linkerd-lb/linkerd-grpc.yaml
$ kubectl apply -f kubernetes/linkerd-lb/echo-server.yaml
$ kubectl apply -f kubernetes/linkerd-lb/echo-client.yaml
```

Find the client instances:
```
$ kubectl get pods -l run=echo-client
```

For each instance, see the logs:
```
$ kubectl logs -f echo-client...
```

Proxy Load Balancing with Istio
-------------------------------
[Istio](https://istio.io) is a Service Mesh that essentially deploys an Envoy proxy per microservice instance.
Istio automatically intercepts the requests and forward the request to a sidecar proxy (i.e., there is a proxy instance
running along side of every microservice instance). The proxy can automatically discover backend instances and
perform L7 load balancing. There is a lot more Istio can do - traffic routing, request retries, ciruit breaking, etc.

1. [bootstrap a Kubernetes cluster with Istio](https://istio.io/docs/setup/kubernetes/quick-start/)
1. [Enable automatic sidecar injection](https://istio.io/docs/setup/kubernetes/sidecar-injection/#automatic-sidecar-injection)

Then, deploy the example:
```
$ kubectl apply -f kubernetes/istio-lb/echo-server.yaml
$ kubectl apply -f kubernetes/istio-lb/echo-client.yaml
```

These files are essentially the same file as the `l4-lb` example. I.e., unlike other examples that require
changes to the code base, when using Istio, L7 load balancing will be automatically applied.

Find the client instances:
```
$ kubectl get pods -l run=echo-client
```

For each instance, see the logs:
```
$ kubectl logs -f echo-client...
```



Other Examples
--------------
* [Spring Boot, Eureka, and gRPC w/ Client-side Load Balancing](https://github.com/saturnism/grpc-java-by-example/tree/master/springboot-example)
* [Zookeeper and gRPC w/ Client-side Load Balancing](https://github.com/makdharma/grpc-zookeeper-lb)

Other Solutions
---------------
* Use a proxy, like [Envoy](https://lyft.github.io/envoy/) or [Linkerd](https://linkerd.io/features/grpc/)
* Use [Istio](https://istio.io/)
