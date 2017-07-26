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

import com.example.grpc.chat.Chat;
import com.example.grpc.chat.ChatServiceGrpc;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rayt on 6/25/17.
 */
@Push
public class ChatUI extends UI {
  private static final Logger logger = Logger.getLogger(ChatUI.class.getName());
  private static final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
      .usePlaintext(true)
      .build();
  private static final ChatServiceGrpc.ChatServiceStub stub = ChatServiceGrpc.newStub(channel);

  @Override
  protected void init(VaadinRequest vaadinRequest) {

    final VerticalLayout layout = new VerticalLayout();

    final TextField name = new TextField();
    name.setCaption("Type your name here:");

    final TextField message = new TextField();
    message.setCaption("Type your message here:");

    Button button = new Button("Send");

    final StreamObserver<Chat.ChatMessage> observer = stub.chat(new StreamObserver<Chat.ChatMessageFromServer>() {
      @Override
      public void onNext(Chat.ChatMessageFromServer chatMessageFromServer) {
        access(() -> {
          layout.addComponent(new Label(String.format("%s: %s",
              chatMessageFromServer.getMessage().getFrom(),
              chatMessageFromServer.getMessage().getMessage())));
        });
      }

      @Override
      public void onError(Throwable throwable) {
        logger.log(Level.SEVERE, "gRPC Error", throwable);
      }

      @Override
      public void onCompleted() {
        logger.info("gRPC Call Completed");
      }
    });

    button.addClickListener(e -> {
      observer.onNext(Chat.ChatMessage.newBuilder()
          .setFrom(name.getValue())
          .setMessage(message.getValue())
          .build());
    });

    layout.addComponents(name, message, button);

    setContent(layout);

  }

  @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
  @VaadinServletConfiguration(ui = ChatUI.class, productionMode = false)
  @WebInitParam(name = "pushmode", value = "automatic")
  public static class ChatUIServlet extends VaadinServlet {
  }
}
