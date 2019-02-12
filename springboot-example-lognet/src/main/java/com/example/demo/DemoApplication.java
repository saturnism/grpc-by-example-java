package com.example.demo;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.jms.JMSException;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

@GRpcService
class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {
	private final MyBusinessService myBusinessService;

	MyServiceImpl(MyBusinessService myBusinessService) {
		this.myBusinessService = myBusinessService;
	}

	@Override public void greeting(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
		try {
			myBusinessService.doSomeWork(request.getName());
			responseObserver.onNext(GreetingResponse.newBuilder().setGreeting("saved " + request.getName()).build());
			responseObserver.onCompleted();
		}
		catch (JMSException e) {
			e.printStackTrace();
			responseObserver.onError(e);
		}
	}

	@Override public void who(WhoRequest request, StreamObserver<WhoResponse> responseObserver) {
		myBusinessService.getNames()
				.stream()
				.map(name -> WhoResponse.newBuilder().setKey(name).build())
				.forEach(responseObserver::onNext);
		responseObserver.onCompleted();
	}
}


