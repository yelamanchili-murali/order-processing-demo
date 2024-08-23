package com.example.orderprocessing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Order {
    private String orderId;
    private String customerId;
    private String orderDetails;
    private String orderStatus;

    @DynamoDbPartitionKey
    public String getOrderId() {
        return orderId;
    }
}
