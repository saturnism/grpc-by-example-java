package com.example.demo;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class MyMessageService {
	private final JmsTemplate jmsTemplate;

	public MyMessageService(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void sendMyMessage(final String payload) throws JMSException {
		jmsTemplate.send("TEST", session -> {
			return session.createTextMessage(payload);
		});
	}
}
