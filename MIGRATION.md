# AWS to Azure service migration guide

This guide offers a high-level overview of the migration process from AWS to Azure. It is intended to guide a Java/Spring Boot user to modify their application to run on Azure. The guide is not exhaustive and may not cover all scenarios. It is recommended to consult the official Azure documentation for more detailed information.

## Scenarios covered in this guide
1. Migrate from DynamoDB to Mongo DB in Azure
2. Migrate from SQS to Azure Service Bus JMS
3. Migrate from SNS to Azure Event Grid (alternatively Azure Event Hubs)

## Sample code snippets for AWS to Azure services
### DynamoDB to Mongo DB

#### DynamoDB 
```java
// Document POJO
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

```

```java
// Configuration
@Bean
public DynamoDbClient dynamoDbClient() {
    return DynamoDbClient.builder()
            .region(Region.of("ap-southeast-2"))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
}
```

```java
// Repository implementation
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
```

```java
// Repository use
public Order createOrder(String customerId, String orderDetails) {
        Order order = new Order();
        order.setOrderId(Utils.generateUUID());
        order.setCustomerId(customerId);
        order.setOrderDetails(orderDetails);
        order.setOrderStatus("CREATED");

        orderRepository.saveOrder(order);
    }
```

#### Equivalent Mongo DB Configuration
```java
// Configuration
@Configuration
public class ApplicationConfig {

    private MongoTemplate mongoTemplate;

    public ApplicationConfig(MongoTemplate mongoTemplate, JmsTemplate jmsTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
```

```java
// Document POJO
@Dynamo(collection = "Orders")
public class Order {
    private String orderId;
    private String customerId;
    private String orderDetails;
    private String orderStatus;

    public String getOrderId() {
        return orderId;
    }
}
```

```java
// MongoDB Repository
@Repository
public interface MongoOrderRepository extends MongoRepository<Order, String> {
    public Optional<Order> findByOrderId(String orderId);
}
```

```java
// Repository use
public Order createOrder(String customerId, String orderDetails) {
        Order newOrder = mongoOrderRepository.save(new Order(Utils.generateUUID(), customerId, orderDetails, "CREATED"));
    }
```

```yaml
# application.yml - MongoDB config
spring:
  application:
    name: order-processing-demo
  data:
    mongodb:
      database: migrationdb
      uri: ${mongoconnection-uri}
```

### SQS to Mongo DB

#### SQS
```java
// Configuration
    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of("ap-southeast-2"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
```
```java
// sending a message to the queue
public void sendMessage(String messageBody) {
        String queueUrl = sqsClient.getQueueUrl(builder -> builder.queueName(queueName)).queueUrl();

        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();

        sqsClient.sendMessage(sendMessageRequest);
    }
```

#### Equivalent Azure Service Bus JMS Configuration
```java
// Configuration - injected JmsTemplate
public class QueueService {

    private final JmsTemplate jmsTemplate;
    
    public QueueService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(QUEUE_NAME, message);
    }
}
```
```yaml
# application.yml - JMS config
spring:
  application:
    name: order-processing-demo
  jms:
    servicebus:
      connection-string: ${servicebusconnection-uri}
      pricing-tier: Standard
    listener:
      receive-timeout: 60000
```

### SNS to Azure Event Grid

#### SNS
```java
// Configuration
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.of("ap-southeast-2"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
```

```java
// notification
public void notify(String message) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .build();

        snsClient.publish(request);
    }
```
#### Equivalent Azure Event Grid Configuration
```java
// Configuration - injected EventGridPublisherClient
public class NotificationService {

    EventGridPublisherClient<EventGridEvent> client;

    public NotificationService(EventGridPublisherClient<EventGridEvent> client) {
        this.client = client;
    }

    public void notify(Order eventData) {
        EventGridEvent eventGridEvent = new EventGridEvent("Order " + eventData + " has been created.", "Order.Created", BinaryData.fromObject(eventData.getOrderId()), "0.1");
        client.sendEvent(eventGridEvent);
    }
}
```

```yaml
# application.yml - SNS config
spring:
  application:
    name: order-processing-demo
  cloud:
    azure:
      eventgrid:
        endpoint: <https://azure-event-grid-endpoint>
```
### pom.xml changes for Azure dependencies
```xml
    <dependency>
        <groupId>com.azure.spring</groupId>
        <artifactId>spring-cloud-azure-starter-servicebus-jms</artifactId>
    </dependency>
    <dependency>
        <groupId>com.azure.spring</groupId>
        <artifactId>spring-cloud-azure-starter-eventgrid</artifactId>
    </dependency>
    <dependency>
        <groupId>com.azure.spring</groupId>
        <artifactId>spring-cloud-azure-starter-keyvault</artifactId>
    </dependency>
```

```xml
<dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.azure.spring</groupId>
                <artifactId>spring-cloud-azure-dependencies</artifactId>
                <version>5.15.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

### Azure Key Vault configuration
```yaml
# application.yml - Key Vault config - use PropertySource to fetch secrets
spring:
  application:
    name: order-processing-demo
  cloud:
    azure:
      keyvault:
        secret:
          property-source-enabled: true
          property-sources:
            - endpoint: https://<keyvaultname>.vault.azure.net/
```