package com.example.orderprocessing.service;

import com.example.orderprocessing.model.Order;
import com.example.orderprocessing.repository.OrderRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

//@Primary
@Service
@ConditionalOnProperty(
        name = "testmode",
        havingValue = "false"
)
public class AwsOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final SqsService sqsService;
    private final SnsService snsService;

    public AwsOrderService(OrderRepository orderRepository, SqsService sqsService, SnsService snsService) {
        this.orderRepository = orderRepository;
        this.sqsService = sqsService;
        this.snsService = snsService;
    }

    @Override
    public Order createOrder(String customerId, String orderDetails) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerId(customerId);
        order.setOrderDetails(orderDetails);
        order.setOrderStatus("CREATED");

        orderRepository.saveOrder(order);

        // Send the order details to SQS
        sqsService.sendMessage("Order created: " + order);

        // Send notification via SNS
        snsService.publishMessage("Order " + order.getOrderId() + " has been created.");

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
