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

import com.example.guestbook.GuestbookServiceGrpc;
import com.example.guestbook.GuestbookServiceOuterClass;
import io.grpc.stub.StreamObserver;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Created by rayt on 6/20/17.
 */
public class GuestbookServiceGrpcImpl extends GuestbookServiceGrpc.GuestbookServiceImplBase {
  private final GuestbookRepository repository;

  public GuestbookServiceGrpcImpl(GuestbookRepository repository) {
    this.repository = repository;
  }

  @Override
  public void all(GuestbookServiceOuterClass.Limit request, StreamObserver<GuestbookServiceOuterClass.GuestbookEntry> responseObserver) {
    Pageable pageable = new PageRequest(request.getPage(), request.getSize());
    repository.findAll(pageable).map(e -> e.toProto())
        .forEach(responseObserver::onNext);
    responseObserver.onCompleted();
  }

  @Override
  public void find(GuestbookServiceOuterClass.FindRequest request, StreamObserver<GuestbookServiceOuterClass.GuestbookEntry> responseObserver) {
    GuestbookEntry entry = repository.findOne(request.getId());
    if (entry != null) {
      responseObserver.onNext(entry.toProto());
    }
    responseObserver.onCompleted();
  }

  @Override
  public void delete(GuestbookServiceOuterClass.DeleteRequest request, StreamObserver<GuestbookServiceOuterClass.DeleteResponse> responseObserver) {
    repository.delete(request.getId());
    responseObserver.onNext(GuestbookServiceOuterClass.DeleteResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
