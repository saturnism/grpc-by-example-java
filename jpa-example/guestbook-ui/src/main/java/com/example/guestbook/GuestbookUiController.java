/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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
package com.example.guestbook;

import com.google.common.collect.Iterables;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;

/**
 * Created by rayt on 5/1/17.
 */
@Controller
public class GuestbookUiController {
  private final GuestbookServiceGrpc.GuestbookServiceBlockingStub guestbookService;

  public GuestbookUiController(GuestbookServiceGrpc.GuestbookServiceBlockingStub guestbookService) {
    this.guestbookService = guestbookService;
  }

  @GetMapping("/")
  public String index(Model model) {

    Iterable<GuestbookEntry> entries = toIterable(guestbookService.all(AllRequest.getDefaultInstance()));
    model.addAttribute("messages", entries);

    return "index";
  }

  @PostMapping("/greet")
  public String greet(@RequestParam String name, @RequestParam String message, Model model) {
    model.addAttribute("name", name);
    if (message != null && !message.trim().isEmpty()) {
      guestbookService.add(AddRequest.newBuilder()
          .setUsername(name)
          .setMessage(message).build());
    }

    return "redirect:/";
  }

  private static <T> Iterable<T> toIterable(final Iterator<T> iterator) {
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return iterator;
      }
    };
  }
}
