package com.example.orderprocessing.service;

import com.example.orderprocessing.model.Order;
import com.example.orderprocessing.repository.OrderRepository;
import com.example.orderprocessing.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

//@Primary
@Service
@ConditionalOnProperty(
        name = "azuremode",
        havingValue = "true"
)
public class AzureOrderService implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureOrderService.class);
    private final OrderRepository orderRepository;
    private final QueueService queueService;
    private final NotificationService notificationService;

    public AzureOrderService(OrderRepository orderRepository, QueueService queueService, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.queueService = queueService;
        this.notificationService = notificationService;
    }

    @Override
    public Order createOrder(String customerId, String orderDetails) {
        Order newOrder = orderRepository.save(new Order(Utils.generateUUID(), customerId, orderDetails, "CREATED"));

        // Send the order details to SQS
        queueService.sendMessage("Order created: " + newOrder);
        // Send notification via SNS
        notificationService.notify(newOrder);

        return newOrder;
    }

    @Override
    public Order getOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId).orElse(null);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();  // Assuming this method is implemented in the repository
    }

}
