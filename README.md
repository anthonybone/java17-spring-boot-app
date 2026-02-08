# Java 17 Spring Boot 3 Lambda REST API

A minimal Java 17 Spring Boot 3 REST API that runs on AWS Lambda using `aws-serverless-java-container-springboot3`. The application is built with Maven and provides a simple health check endpoint.

## Features

- **Java 17** with Spring Boot 3.2.0
- **AWS Lambda** integration using AWS Serverless Java Container
- **REST API** with GET /health endpoint returning `{"status": "ok"}`
- **Maven** build system
- **No dependencies** on DB, authentication, Docker, Terraform, or AWS SDK

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

If Maven is using an older JDK, you can run a one-off build with JDK 17 in bash:

```bash
JAVA_HOME="/c/Program Files/Java/jdk-17.0.1" PATH="$JAVA_HOME/bin:$PATH" mvn clean package
```

## Project Structure

```
.
├── pom.xml                                    # Maven configuration
├── src/main/java/com/example/
│   ├── Application.java                       # Spring Boot application entry point
│   ├── HealthController.java                  # REST controller with /health endpoint
│   └── StreamLambdaHandler.java              # Lambda handler for API Gateway requests
├── src/main/resources/
│   └── application.properties                 # Spring Boot configuration
└── event.json                                 # Sample API Gateway event for local testing
```

## Building the Application

Build the application using Maven:

```bash
mvn clean package
```

This creates a fat JAR at `target/spring-boot-lambda-1.0.0.jar` that includes all dependencies needed for Lambda execution.

## Running Locally (Standard Spring Boot)

You can run the application as a standard Spring Boot app for quick local testing:

```bash
mvn spring-boot:run
```

Then test the health endpoint:

```bash
curl http://localhost:8080/health
```

Expected response:
```json
{"status":"ok"}
```

## Invoking the Lambda Handler Locally

### Method 1: Using AWS SAM CLI (Recommended)

Install [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html), then:

```bash
# Build the application
mvn clean package

# Invoke the Lambda function with the sample event
sam local invoke -t sam-template.yaml --event event.json
```

### Method 2: Using Java Directly

You can test the Lambda handler by creating a simple Java test class:

```java
import com.example.StreamLambdaHandler;
import com.amazonaws.services.lambda.runtime.Context;
import java.io.*;

public class LocalTest {
    public static void main(String[] args) throws IOException {
        StreamLambdaHandler handler = new StreamLambdaHandler();
        
        // Load the event.json file
        InputStream input = new FileInputStream("event.json");
        OutputStream output = new ByteArrayOutputStream();
        
        // Create a mock context (can be null for basic testing)
        Context context = null;
        
        // Invoke the handler
        handler.handleRequest(input, output, context);
        
        // Print the response
        System.out.println(output.toString());
    }
}
```

### Method 3: Using AWS Lambda Runtime Interface Emulator (RIE)

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Run with AWS Lambda RIE:
   ```bash
   aws-lambda-rie java -cp target/spring-boot-lambda-1.0.0.jar com.example.StreamLambdaHandler::handleRequest
   ```

3. Send a test request:
   ```bash
   curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d @event.json
   ```

## API Endpoints

### GET /health

Returns the health status of the application.

**Request:**
```bash
curl http://localhost:8080/health
```

**Response:**
```json
{"status":"ok"}
```

## Deploying to AWS Lambda

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Upload `target/spring-boot-lambda-1.0.0.jar` to AWS Lambda

3. Configure the Lambda function:
   - **Runtime:** Java 17
   - **Handler:** `com.example.StreamLambdaHandler::handleRequest`
   - **Memory:** 512 MB (minimum recommended)
   - **Timeout:** 30 seconds

4. Create an API Gateway REST API and configure it to trigger your Lambda function with proxy integration

## Lambda Handler

The `StreamLambdaHandler` class handles API Gateway proxy requests and routes them to the Spring Boot application:

- Initializes Spring Boot context once (on cold start)
- Proxies API Gateway requests to Spring MVC
- Automatically handles request/response transformation

## Dependencies

Key dependencies used:
- `spring-boot-starter-web` (3.2.0) - Spring Boot web framework (without Tomcat)
- `aws-serverless-java-container-springboot3` (2.0.0) - AWS Lambda Spring Boot adapter
- `aws-lambda-java-core` (1.2.3) - AWS Lambda Java runtime interface
- `aws-lambda-java-events` (3.11.3) - AWS Lambda event models

## Notes

- The application excludes Tomcat since Lambda doesn't need an embedded server
- Maven Shade plugin creates a fat JAR with all dependencies
- Cold start time is optimized by using Spring Boot 3's native hints support
- The handler uses stream processing for efficient memory usage
