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
public class TranslationServer {
  static public void main(String [] args) throws IOException, InterruptedException {
    JwtServerInterceptor jwtInterceptor = new JwtServerInterceptor(Constant.JWT_SECRET);

    Server server = ServerBuilder.forPort(9095)
        .addService(ServerInterceptors.intercept(new TranslationServiceImpl(), new GreetingServer.MetadataServerInterceptor()))
        .build();
    server.start();

    System.out.println("Server started!");
    server.awaitTermination();
  }

  public static class TranslationServiceImpl extends TranslationServiceGrpc.TranslationServiceImplBase {
    @Override
    public void translate(TranslationRequest request, StreamObserver<TranslationResponse> responseObserver) {
      TranslationResponse response = TranslationResponse.newBuilder().setMessage(request.getMessage() + request.getTo().toString()).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }

}
