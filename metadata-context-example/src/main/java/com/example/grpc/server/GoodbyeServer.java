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
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

/**
 * Created by rayt on 5/16/16.
 */
public class GoodbyeServer {
  static public void main(String[] args) throws IOException, InterruptedException {
    JwtServerInterceptor jwtInterceptor = new JwtServerInterceptor(Constant.JWT_SECRET);

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext(true)
        .intercept(new JwtClientInterceptor())
        .intercept(new TraceIdClientInterceptor())
        .build();
    GreetingServiceGrpc.GreetingServiceBlockingStub greetingStub = GreetingServiceGrpc.newBlockingStub(channel);

    Server goodbyeServer = ServerBuilder.forPort(9090)
        .addService(ServerInterceptors.intercept(new GoodbyeServiceImpl(greetingStub), jwtInterceptor, new TraceIdServerInterceptor()))
        .build();
    goodbyeServer.start();

    System.out.println("Server started!");
    goodbyeServer.awaitTermination();
  }

  public static class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    private final ManagedChannel channel;
    private final GoodbyeServiceGrpc.GoodbyeServiceBlockingStub goodbyeStub;

    public GreetingServiceImpl(ManagedChannel channel) {
      this.channel = channel;
      this.goodbyeStub = GoodbyeServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void greeting(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
      System.out.println(request);

      String userId = Constant.USER_ID_CTX_KEY.get();
      System.out.println("Greeting Service User ID: " + userId);

      String greeting = "Hello there, " + request.getName() + ", your userId is " + userId;

      HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void secondGreeting(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
      GoodbyeResponse goodbye = goodbyeStub.goodbye(GoodbyeRequest.newBuilder().setName(request.getName()).build());
      System.out.println("Say Goodbye! " + goodbye);
      greeting(request, responseObserver);
    }
  }

  public static class GoodbyeServiceImpl extends GoodbyeServiceGrpc.GoodbyeServiceImplBase {
    private final GreetingServiceGrpc.GreetingServiceBlockingStub greetingStub;

    public GoodbyeServiceImpl(GreetingServiceGrpc.GreetingServiceBlockingStub greetingStub) {
      this.greetingStub = greetingStub;
    }

    @Override
    public void goodbye(GoodbyeRequest request, StreamObserver<GoodbyeResponse> responseObserver) {
      String userId = Constant.USER_ID_CTX_KEY.get();
      System.out.println("Goodbye Service Trace ID: " + Constant.TRACE_ID_CTX_KEY.get());
      System.out.println("Goodbye Service User ID: " + userId);

      HelloResponse helloResponse = this.greetingStub.greeting(HelloRequest.newBuilder().setName(request.getName()).build());
      System.out.println(helloResponse);


      String farewell = "Goodye, " + request.getName() + ", your userId is " + userId;
      responseObserver.onNext(GoodbyeResponse.newBuilder().setFarewell(farewell).build());
      responseObserver.onCompleted();
    }
  }
}
