package com.example.orderprocessing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class ApplicationConfig {

    private MongoTemplate mongoTemplate;

    public ApplicationConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
