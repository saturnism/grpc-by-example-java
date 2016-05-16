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

package com.example.grpc.chat;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;

import java.util.LinkedHashSet;

/**
 * Created by rayt on 5/16/16.
 */
public class ChatServiceImpl implements ChatServiceGrpc.ChatService {
  private static LinkedHashSet<StreamObserver<Chat.ChatMessageFromServer>> observers = new LinkedHashSet<>();

  @Override
  public StreamObserver<Chat.ChatMessage> chat(StreamObserver<Chat.ChatMessageFromServer> responseObserver) {
    observers.add(responseObserver);

    return new StreamObserver<Chat.ChatMessage>() {
      @Override
      public void onNext(Chat.ChatMessage value) {
        System.out.println(value);
        Chat.ChatMessageFromServer message = Chat.ChatMessageFromServer.newBuilder()
            .setMessage(value)
            .setTimestamp(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000))
            .build();

        for (StreamObserver<Chat.ChatMessageFromServer> observer : observers) {
          observer.onNext(message);
        }
      }

      @Override
      public void onError(Throwable t) {
        observers.remove(responseObserver);
        responseObserver.onError(t);
      }

      @Override
      public void onCompleted() {
        observers.remove(responseObserver);
        responseObserver.onCompleted();
      }
    };
  }
}
