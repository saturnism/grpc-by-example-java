package com.example.greeting;

import com.example.greeting.v1.GreetingRequest;
import com.example.greeting.v1.GreetingResponse;
import com.example.greeting.v1.GreetingServiceGrpc;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
	@Override public void greet(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
		responseObserver.onNext(GreetingResponse.newBuilder()
				.setGreeting("Hello " + request.getName())
				.build());

		responseObserver.onCompleted();
	}
}
