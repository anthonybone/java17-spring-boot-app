package com.example;

import com.amazonaws.services.lambda.runtime.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Simple utility to test the Lambda handler locally.
 * This simulates invoking the handler with an API Gateway request.
 */
public class LocalLambdaTest {
    public static void main(String[] args) throws IOException {
        StreamLambdaHandler handler = new StreamLambdaHandler();
        
        // Create a simple API Gateway GET request to /health as JSON
        String requestJson = """
                {
                  "resource": "/health",
                  "path": "/health",
                  "httpMethod": "GET",
                  "headers": {
                    "Accept": "application/json",
                    "Content-Type": "application/json"
                  },
                  "queryStringParameters": null,
                  "pathParameters": null,
                  "stageVariables": null,
                  "requestContext": {
                    "accountId": "123456789012",
                    "resourceId": "abc123",
                    "stage": "prod",
                    "requestId": "test-request-id",
                    "identity": {
                      "sourceIp": "127.0.0.1"
                    },
                    "resourcePath": "/health",
                    "httpMethod": "GET",
                    "apiId": "test-api-id"
                  },
                  "body": null,
                  "isBase64Encoded": false
                }
                """;
        
        // Prepare input and output streams
        InputStream input = new ByteArrayInputStream(requestJson.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        // Create a simple mock context (null is acceptable for basic testing)
        Context context = null;
        
        // Invoke the handler
        System.out.println("Invoking Lambda handler with GET /health request...");
        System.out.println("Request:");
        System.out.println(requestJson);
        
        handler.handleRequest(input, output, context);
        
        // Print the response
        String response = output.toString(StandardCharsets.UTF_8);
        System.out.println("\nResponse from Lambda handler:");
        System.out.println(response);
    }
}
