package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import java.util.List;
import java.util.function.Consumer;

@Service
public class MyBusinessService {
	private final MyDataRepository myDataRepository;
	private final MyMessageService myMessageService;

	public MyBusinessService(MyDataRepository myDataRepository, MyMessageService myMessageService) {
		this.myDataRepository = myDataRepository;
		this.myMessageService = myMessageService;
	}

	@Transactional
	public void doSomeWork(String request) throws JMSException {
		myDataRepository.insert(request, "some json payload");
		myMessageService.sendMyMessage(request);
	}

	public List<String> getNames() {
		return myDataRepository.findAll();
	}
}
