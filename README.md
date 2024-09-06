# Order Processing Demo

This is a Spring Boot 3 application demonstrating an order processing system using AWS DynamoDB, SQS, and SNS. The application includes a simple UI to create and view orders.

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- AWS CLI configured with appropriate credentials and region
- Local DynamoDB setup (optional, if you prefer to run DynamoDB locally)
- AWS Account with permissions to create and manage DynamoDB, SQS, and SNS resources

## AWS Resources

### 1. DynamoDB Table

The application uses a DynamoDB table named `Orders` with `orderId` as the partition key. You can create this table using the AWS CLI:

```bash
aws dynamodb create-table \
    --table-name Orders \
    --attribute-definitions AttributeName=orderId,AttributeType=S \
    --key-schema AttributeName=orderId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --region ap-southeast-2
```
### 2. SQS Queue
The application uses an SQS queue named OrderQueue for order processing. Create this queue using the AWS CLI:
```
aws sqs create-queue --queue-name OrderQueue --region ap-southeast-2
```

### 3. SNS Topic
The application uses an SNS topic named OrderNotifications to send notifications when an order is created. Create this topic using the AWS CLI:
```
aws sns create-topic --name OrderNotifications --region ap-southeast-2
```

You can also subscribe to this topic to receive notifications via email or SMS.

#### Email Subscription:

```
aws sns subscribe --topic-arn arn:aws:sns:ap-southeast-2:YOUR_AWS_ACCOUNT_ID:OrderNotifications \
  --protocol email --notification-endpoint your-email@example.com --region ap-southeast-2
```

**Note**: Replace YOUR_AWS_ACCOUNT_ID, your-email@example.com, and +1234567890 with your actual AWS account ID, email, and phone number, respectively.

## Running the Application

1. Clone the repository:
```
git clone https://github.com/your-username/order-processing-demo.git
cd order-processing-demo
```

2. Configure AWS Credentials: Ensure that your AWS credentials are configured correctly. You can do this by running:
```
aws configure
```

3. Run the application:
```
mvn spring-boot:run
```

4. Access the UI: Open a web browser and navigate to `http://localhost:8080` to access the application.

5. Running in offline/test mode

Add `testmode: true` to the `application.yml` to enable an offline mode using a mock service.
#### Additional Information

* DynamoDB Local: If you prefer to run DynamoDB locally, ensure you update the application.yml with the appropriate endpoint URL.
* Security: Avoid committing AWS credentials or other sensitive information to your repository. Use environment variables or AWS IAM roles for secure access.

## Post-migration: Running with Azure services
Post-migration, create an Azure Service principal and provide the Client Id, Client Secret and Azure tenant id as JVM options. 
This allows the application to connect to Key vault to retrieve the secrets and bind to the property placeholders for Servicebus and Mongo conenction URIs. 
JVM Options: `-DAZURE_CLIENT_ID=<client-id> -DAZURE_CLIENT_SECRET=<client-secret> -DAZURE_TENANT_ID=<azure-tenant-id>`
