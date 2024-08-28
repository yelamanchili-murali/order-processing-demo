package com.example.orderprocessing.service;

import com.example.orderprocessing.model.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(String customerId, String orderDetails);

    Order getOrderById(String orderId);

    List<Order> getAllOrders();
}
