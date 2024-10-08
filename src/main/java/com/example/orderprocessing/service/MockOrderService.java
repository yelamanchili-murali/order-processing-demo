package com.example.orderprocessing.service;

import com.example.orderprocessing.model.Order;
import com.example.orderprocessing.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnProperty(
        name = "mockmode",
        havingValue = "true"
)
public class MockOrderService implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockOrderService.class);
    // initialise a hashmap of orders
    // key: order id, value: order object
    private Map<String, Order> orders = new HashMap<String, Order>();

    public MockOrderService() {
        // add a mock order to the hashmap
        orders.put("558a566f-854f-462f-ab36-6bbcdc725382", new Order("558a566f-854f-462f-ab36-6bbcdc725382",
                    "CUST123",
                "Mockitem1,Mockitem2",
                "CREATED"));
    }

    @Override
    public Order createOrder(String customerId, String orderDetails) {
        String orderId = Utils.generateUUID();
        return orders.put(orderId, new Order(orderId,
                customerId,
                orderDetails,
                "CREATED"));
    }

    @Override
    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orders.values().stream().toList();
    }
}
