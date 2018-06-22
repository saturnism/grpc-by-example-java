gRPC in Docker Compose
=========================================
This example uses the container built in the [Kubernetes Load Balancing Example](../kubernetes-lb-example).

L4 Load Balancing
-----------------
Use Docker Compose to setup a simple linked containers. However, becareful about L4 load balancing in gRPC.

gRPC client can connect to the service IP (and with DNS name) directly.
Every time a new connection is opened, it'll be load balanced across
the running server instances.

Deploy the example:
```
$ docker-compose -f l4-lb-example.yaml up
```
