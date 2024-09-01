package com.example.orderprocessing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Orders")
public class Order {
    private String orderId;
    private String customerId;
    private String orderDetails;
    private String orderStatus;

    public String getOrderId() {
        return orderId;
    }
}
