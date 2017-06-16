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

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is a simple client that depends on external load balancing, either via a proxy,
 * or a L4/L7 load balancer.
 */
public class SimpleEchoClient {
  private static int THREADS = 4;

  public static void main(String[] args) throws InterruptedException, UnknownHostException {
    String target = System.getenv("ECHO_SERVICE_TARGET");
    if (target == null || target.isEmpty()) {
      target = "localhost:8080";
    }
    final ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        .usePlaintext(true)
        .build();

    final String host = InetAddress.getLocalHost().getHostName();

    ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
    for (int i = 0; i < THREADS; i++) {
      EchoServiceGrpc.EchoServiceBlockingStub stub = EchoServiceGrpc.newBlockingStub(channel);
      executorService.submit(() -> {
        while (true) {
          EchoResponse response = stub.echo(EchoRequest.newBuilder()
              .setMessage(host + ": " + Thread.currentThread().getName())
              .build());
          System.out.println(response.getFrom() + " echoed");

          Thread.sleep(700);
        }
      });
    }
  }
}
