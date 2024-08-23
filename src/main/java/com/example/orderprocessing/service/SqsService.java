package com.example.orderprocessing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class SqsService {

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queueName}")
    private String queueName;

    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void sendMessage(String messageBody) {
        String queueUrl = sqsClient.getQueueUrl(builder -> builder.queueName(queueName)).queueUrl();

        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();

        sqsClient.sendMessage(sendMessageRequest);
    }
}
