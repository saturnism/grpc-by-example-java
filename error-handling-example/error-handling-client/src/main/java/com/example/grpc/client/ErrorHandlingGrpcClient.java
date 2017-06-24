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

import com.example.grpc.error.EchoRequest;
import com.example.grpc.error.ErrorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rayt on 5/16/16.
 */
public class ErrorHandlingGrpcClient {
  private static final Logger logger = Logger.getLogger(ErrorHandlingGrpcClient.class.getName());
  public static void main(String[] args) throws InterruptedException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext(true)
        .build();

    final EchoRequest request = EchoRequest.getDefaultInstance();

    ErrorServiceGrpc.ErrorServiceBlockingStub stub = ErrorServiceGrpc.newBlockingStub(channel);

    // Deadline exceeded
    // Server-side can listen to Cancellations
    try {
      stub.withDeadlineAfter(2, TimeUnit.SECONDS).deadlineExceeded(request);
    } catch (StatusRuntimeException e) {
      // Do not use Status.equals(...) - it's not well defined. Compare Code directly.
      if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
        logger.log(Level.SEVERE, "Deadline exceeded!", e);
      }
    }

    // Server-side forgot to implement an operation
    try {
      stub.notImplemented(request);
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.Code.UNIMPLEMENTED) {
        logger.log(Level.SEVERE, "Operation not implemented", e);
      }
    }

    // Server-side throw an NPE, but client wouldn't know
    try {
      stub.uncaughtExceptions(request);
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.Code.UNKNOWN) {
        logger.log(Level.SEVERE, "Server threw an exception... Not sure which one!", e);
      }
    }

    // Server-side called observer.onNext(new CustomException())
    try {
      stub.customUnwrapException(request);
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.Code.UNKNOWN) {
        logger.log(Level.SEVERE, "Server threw another exception... Not sure which one!", e);
      }
    }

    // Server-side wrapped the CustomException in a StatusRuntimeException
    try {
      stub.customException(request);
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.Code.INTERNAL) {
        logger.log(Level.SEVERE, e.getMessage(), e);
      }
    }

    // Server-side automatically wrapped the IllegalArgumentException via an interceptor
    try {
      stub.automaticallyWrappedException(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
    }

    channel.shutdown();
  }
}
