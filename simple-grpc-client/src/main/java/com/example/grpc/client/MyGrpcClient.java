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

package com.example.grpc.client;

import com.example.server.GreetingRequest;
import com.example.server.GreetingResponse;
import com.example.server.GreetingServiceGrpc;
import com.example.server.Sentiment;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.SimpleLoadBalancerFactory;
import rx.internal.operators.BufferUntilSubscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by rayt on 5/16/16.
 */
public class MyGrpcClient {
  public static void main(String[] args) throws InterruptedException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .loadBalancerFactory(SimpleLoadBalancerFactory.getInstance())
        .usePlaintext(true)
        .build();

    GreetingServiceGrpc.GreetingServiceBlockingStub greetingService = GreetingServiceGrpc.newBlockingStub(channel);
    Map<String, String> tricks = new HashMap<>();
    tricks.put("live-coding", "not so great");

    GreetingRequest request = GreetingRequest.newBuilder()
        .setAge(20).setName("Ray")
        .setSentiment(Sentiment.ANGRY).putAllBagOfTricks(tricks).build();
    GreetingResponse response = greetingService.greet(request);
    System.out.println(response.getGreeting());

    GreetingServiceGrpc.GreetingServiceStub asyncStub = GreetingServiceGrpc.newStub(channel);
    BufferUntilSubscriber<GreetingResponse> subject = BufferUntilSubscriber.create();
    asyncStub.greet(request, new RxStreamObserver<GreetingResponse>() {
      @Override
      public void onNext(GreetingResponse greetingResponse) {
        subject.onNext(greetingResponse);
      }

      @Override
      public void onError(Throwable throwable) {
        subject.onError(throwable);
      }

      @Override
      public void onCompleted() {
        subject.onCompleted();
      }
    });

    System.out.println(subject.count());

    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }
}
