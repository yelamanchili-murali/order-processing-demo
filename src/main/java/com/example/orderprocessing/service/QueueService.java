package com.example.orderprocessing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueService.class);
    private final JmsTemplate jmsTemplate;
    private final static String QUEUE_NAME = "OrderQueue";

    public QueueService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(QUEUE_NAME, message);
    }
}
