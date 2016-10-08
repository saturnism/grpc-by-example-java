/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.grpc.server;

import com.example.grpc.*;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.grpc.BraveGrpcClientInterceptor;
import com.github.kristofa.brave.grpc.BraveGrpcServerInterceptor;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import io.prometheus.client.CollectorRegistry;
import me.dinowernli.grpc.prometheus.Configuration;
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor;

import java.io.IOException;

/**
 * Created by rayt on 5/16/16.
 */
public class GoodbyeServer {
  static public void main(String[] args) throws IOException, InterruptedException {
    Brave brave = Constant.brave("goodbye-service");
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext(true)
        .intercept(new BraveGrpcClientInterceptor(brave))
        .build();
    GreetingServiceGrpc.GreetingServiceBlockingStub greetingStub = GreetingServiceGrpc.newBlockingStub(channel);

    Server goodbyeServer = ServerBuilder.forPort(9090)
        .addService(ServerInterceptors.intercept(new GoodbyeServiceImpl(greetingStub),
            new BraveGrpcServerInterceptor(brave),
            MonitoringServerInterceptor.create(Configuration.allMetrics())))
        .build();

    goodbyeServer.start();

    PrometheusServer prometheusServer = new PrometheusServer(CollectorRegistry.defaultRegistry, 8081);
    prometheusServer.start();

    System.out.println("Server started!");
    goodbyeServer.awaitTermination();
    prometheusServer.shutdown();
  }

  public static class GoodbyeServiceImpl extends GoodbyeServiceGrpc.GoodbyeServiceImplBase {
    private final GreetingServiceGrpc.GreetingServiceBlockingStub greetingStub;

    public GoodbyeServiceImpl(GreetingServiceGrpc.GreetingServiceBlockingStub greetingStub) {
      this.greetingStub = greetingStub;
    }

    @Override
    public void goodbye(GoodbyeRequest request, StreamObserver<GoodbyeResponse> responseObserver) {
      HelloResponse helloResponse = this.greetingStub.greeting(HelloRequest.newBuilder().setName(request.getName()).build());
      System.out.println(helloResponse);


      String farewell = helloResponse.getGreeting() + ", and goodbye, " + request.getName();
      responseObserver.onNext(GoodbyeResponse.newBuilder().setFarewell(farewell).build());
      responseObserver.onCompleted();
    }
  }
}
