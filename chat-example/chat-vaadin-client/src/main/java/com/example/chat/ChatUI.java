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
package com.example.chat;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.grpc.chat.Chat;
import com.example.grpc.chat.ChatServiceGrpc;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Created by rayt on 6/25/17.
 */
@Push
@PreserveOnRefresh
public class ChatUI extends UI {

  private static final Logger logger = Logger.getLogger(ChatUI.class.getName());
  private static final ManagedChannel channel = ManagedChannelBuilder
      .forAddress("localhost" , 9090)
      .usePlaintext(true)
      .build();

  private static final ChatServiceGrpc.ChatServiceStub stub = ChatServiceGrpc.newStub(channel);


  private final VerticalLayout layout = new VerticalLayout();
  private final TextField name = new TextField();
  private final TextField message = new TextField();
  private final Button button = new Button("Send");


  public ChatUI() {

    name.setId("tf.name");
    name.setCaption("Type your name here:");

    message.setId("tf.message");
    message.setCaption("Type your message here:");

    layout.addComponents(name , message , button);
    setContent(layout);
  }

  @Override
  protected void init(VaadinRequest vaadinRequest) {

    final UI currentUI = getCurrent();
    final StreamObserver<Chat.ChatMessage> observer = stub.chat(new StreamObserver<Chat.ChatMessageFromServer>() {
      @Override
      public void onNext(Chat.ChatMessageFromServer chatMessageFromServer) {
        currentUI.access(() -> layout.addComponent(new Label(String.format("%s: %s" ,
                                                                           chatMessageFromServer.getMessage().getFrom() ,
                                                                           chatMessageFromServer.getMessage().getMessage()))));
      }

      @Override
      public void onError(Throwable throwable) {
        logger.log(Level.SEVERE , "gRPC Error" , throwable);
      }

      @Override
      public void onCompleted() {
        logger.info("gRPC Call Completed");
      }
    });

    button.addClickListener(e -> {

      final String nameValue = name.getValue();
      final String messageValue = message.getValue();

      observer.onNext(Chat.ChatMessage.newBuilder()
                                      .setFrom(nameValue)
                                      .setMessage(messageValue)
                                      .build());
    });

  }

}
