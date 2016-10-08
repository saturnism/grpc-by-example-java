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

import com.example.grpc.Constant;
import com.example.grpc.GreetingServiceGrpc;
import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.grpc.BraveGrpcServerInterceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.stub.StreamObserver;
import me.dinowernli.grpc.prometheus.Configuration;
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor;

import java.io.IOException;

/**
 * Created by rayt on 5/16/16.
 */
public class GreetingServer {
  static public void main(String [] args) throws IOException, InterruptedException {
    Brave brave = Constant.brave("greeting-service");
    Server greetingServer = ServerBuilder.forPort(8080)
        .addService(ServerInterceptors.intercept(new GreetingServiceImpl(),
            new BraveGrpcServerInterceptor(brave),
            MonitoringServerInterceptor.create(Configuration.allMetrics())))
        .build();
    greetingServer.start();

    System.out.println("Server started!");
    greetingServer.awaitTermination();
  }

  public static class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greeting(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
      System.out.println(request);

      String greeting = "Hello there, " + request.getName();
      HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }
}
