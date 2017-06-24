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

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by rayt on 5/16/16.
 */
public class ErrorGrpcServer {
  static public void main(String[] args) throws IOException, InterruptedException {
    UnknownStatusDescriptionInterceptor unknownStatusDescriptionInterceptor = new UnknownStatusDescriptionInterceptor(Arrays.asList(
        IllegalArgumentException.class
    ));
    Server server = ServerBuilder.forPort(8080)
        .addService(ServerInterceptors.intercept(new ErrorServiceImpl(), unknownStatusDescriptionInterceptor))
        .build();

    System.out.println("Starting server...");
    server.start();
    System.out.println("Server started!");
    server.awaitTermination();
  }
}
