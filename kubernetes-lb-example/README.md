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
$ kubectl apply -f kubernetes/l4-lb/
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

Client-side Load Balancing
--------------------------
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
$ kubectl apply -f kubernetes/client-side-lb/
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


