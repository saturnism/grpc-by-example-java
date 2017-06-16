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

import com.example.grpc.EchoRequest;
import com.example.grpc.EchoResponse;
import com.example.grpc.EchoServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Created by rayt on 5/16/16.
 */
public class EchoServer {
  static public void main(String[] args) throws IOException, InterruptedException {

    Server server = ServerBuilder.forPort(8080)
        .addService(new EchoServiceImpl()).build();

    System.out.println("Starting server...");
    server.start();
    System.out.println("Server started!");
    server.awaitTermination();
  }
}

class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
  private static Logger LOGGER = Logger.getLogger(EchoServiceImpl.class.getName());

  @Override
  public void echo(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
    try {
      String from = InetAddress.getLocalHost().getHostAddress();
      System.out.println("Received: " + request.getMessage());
      responseObserver.onNext(EchoResponse.newBuilder()
          .setFrom(from)
          .setMessage(request.getMessage())
          .build());
      responseObserver.onCompleted();
    } catch (UnknownHostException e) {
      responseObserver.onError(e);
    }
  }
}
