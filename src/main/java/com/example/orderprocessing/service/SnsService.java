package com.example.orderprocessing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class SnsService {

    private final SnsClient snsClient;

    @Value("${aws.sns.topicName}")
    private String topicName;

    public SnsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void publishMessage(String message) {
        String topicArn = snsClient.createTopic(builder -> builder.name(topicName)).topicArn();

        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .build();

        snsClient.publish(publishRequest);
    }
}
