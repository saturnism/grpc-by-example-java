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

import com.example.guestbook.*;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.autoconfigure.grpc.server.GrpcService;

/**
 * Created by rayt on 6/20/17.
 */
@GrpcService
public class GuestbookServiceGrpcImpl extends GuestbookServiceGrpc.GuestbookServiceImplBase {
  private final GuestbookRepository repository;

  public GuestbookServiceGrpcImpl(GuestbookRepository repository) {
    this.repository = repository;
  }

  @Override
  public void all(AllRequest request, StreamObserver<GuestbookEntry> responseObserver) {
    repository.findAll().forEach(e -> {
      responseObserver.onNext(e.toProto());
    });
    responseObserver.onCompleted();
  }

  @Override
  public void findOne(FindOneRequest request, StreamObserver<GuestbookEntry> responseObserver) {
    GuestbookEntryDomain entry = repository.findOne(request.getId());
    if (entry != null) {
      responseObserver.onNext(entry.toProto());
    }
    responseObserver.onCompleted();
  }

  @Override
  public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
    repository.delete(request.getId());
    responseObserver.onNext(DeleteResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }

  @Override
  public void add(AddRequest request, StreamObserver<AddResponse> responseObserver) {
    GuestbookEntryDomain entry = GuestbookEntryDomain.fromProto(request);
    entry = repository.save(entry);
    responseObserver.onNext(AddResponse.newBuilder()
        .setId(entry.getId())
        .build());
    responseObserver.onCompleted();
  }
}
