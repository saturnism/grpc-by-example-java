/*
 * Copyright 2017 Google, Inc.
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

package com.example.grpc.springboot;

import com.example.guestbook.AddRequest;
import com.example.guestbook.GuestbookEntry;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by rayt on 6/20/17.
 */
@Entity
public class GuestbookEntryDomain {
  @Id
  @GeneratedValue
  private Long id;

  private String username;
  private String message;

  public static GuestbookEntryDomain fromProto(GuestbookEntry proto) {
    GuestbookEntryDomain entry = new GuestbookEntryDomain();
    entry.setId(proto.getId());
    entry.setUsername(proto.getUsername());
    entry.setMessage(proto.getMessage());
    return entry;
  }

  public static GuestbookEntryDomain fromProto(AddRequest proto) {
    GuestbookEntryDomain entry = new GuestbookEntryDomain();
    entry.setUsername(proto.getUsername());
    entry.setMessage(proto.getMessage());
    return entry;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public GuestbookEntry toProto() {
    return GuestbookEntry.newBuilder()
        .setId(getId())
        .setUsername(getUsername())
        .setMessage(getMessage())
        .build();
  }
}
