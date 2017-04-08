Prerequisite
============
* `mvn install` my [Spring Boot gRPC Starter](https://github.com/saturnism/spring-boot-starter-grpc)

Build
=====
* `mvn install`

Run
===
1. Start Eureka: `java -jar eureka-server/target/eureka-server-1.0-SNAPSHOT.jar`
1. Start gRPC Server #1: `java -jar grpc-server/target/springboot-grpc-server-1.0-SNAPSHOT.jar --server.port=9090`
1. Start gRPC Server #2: `java -jar grpc-server/target/springboot-grpc-server-1.0-SNAPSHOT.jar --server.port=9091`
1. Start gRPC Client: `java -jar target/springboot-grpc-client-1.0-SNAPSHOT.jar`
