package com.example.orderprocessing.service;

import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.example.orderprocessing.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    private final EventGridPublisherClient<EventGridEvent> client;

    public NotificationService(EventGridPublisherClient<EventGridEvent> client) {
        this.client = client;
    }

    public void notify(Order eventData) {
        EventGridEvent eventGridEvent = new EventGridEvent("Order " + eventData + " has been created.", "Order.Created", BinaryData.fromObject(eventData.getOrderId()), "0.1");
        client.sendEvent(eventGridEvent);
    }
}
