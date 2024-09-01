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
        name = "awsmode",
        havingValue = "true"
)
public class AwsOrderService implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsOrderService.class);
    private final OrderRepository orderRepository;
    private final QueueService queueService;
    private final NotificationService notificationService;

    public AwsOrderService(OrderRepository orderRepository, QueueService queueService, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.queueService = queueService;
        this.notificationService = notificationService;
    }

    @Override
    public Order createOrder(String customerId, String orderDetails) {
        Order order = new Order();
        order.setOrderId(Utils.generateUUID());
        order.setCustomerId(customerId);
        order.setOrderDetails(orderDetails);
        order.setOrderStatus("CREATED");

        orderRepository.saveOrder(order);

        // Send the order details to SQS
        queueService.sendMessage("Order created: " + order);

        // Send notification via SNS
        notificationService.notify(order);

        return order;
    }

    @Override
    public Order getOrderById(String orderId) {
        return orderRepository.getOrderById(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();  // Assuming this method is implemented in the repository
    }

}
