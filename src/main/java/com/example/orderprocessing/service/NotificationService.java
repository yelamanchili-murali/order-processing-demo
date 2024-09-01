package com.example.orderprocessing.service;

import com.example.orderprocessing.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    private final SnsClient snsClient;

    @Value("${aws.sns.topicName}")
    private String topicName;

    public NotificationService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void notify(Order eventData) {
        String topicArn = snsClient.createTopic(builder -> builder.name(topicName)).topicArn();

        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .message("Order " + eventData.getOrderId() + " has been created.")
                .build();

        snsClient.publish(publishRequest);
    }
}
