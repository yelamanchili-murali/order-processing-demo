package com.example.orderprocessing.repository;

import com.example.orderprocessing.model.Order;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {

    private final DynamoDbTable<Order> orderTable;

    public OrderRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.orderTable = dynamoDbEnhancedClient.table("Orders", TableSchema.fromBean(Order.class));
    }

    public void saveOrder(Order order) {
        orderTable.putItem(order);
    }

    public Order getOrderById(String orderId) {
        return orderTable.getItem(r -> r.key(k -> k.partitionValue(orderId)));
    }

    public List<Order> getAllOrders() {
        return orderTable.scan().items().stream().collect(Collectors.toList());
    }
}
