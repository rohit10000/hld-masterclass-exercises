package com.example.long_short_polling.client;

import com.example.long_short_polling.model.EC2Instance;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

// LongPollingClient.java - Client implementation for long polling
public class LongPollingClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public LongPollingClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String createInstance() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/ec2/create"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public EC2Instance longPollInstanceStatus(String instanceId, int timeoutSeconds) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/ec2/status/" + instanceId + "/long-poll?timeoutSeconds=" + timeoutSeconds))
                .timeout(Duration.ofSeconds(timeoutSeconds + 5)) // Client timeout slightly longer
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), EC2Instance.class);
    }

    // Long polling with retry logic for connection issues
    public void waitForInstanceRunning(String instanceId) throws Exception {
        System.out.println("Starting long polling for instance: " + instanceId);

        int maxRetries = 3;
        for (int retry = 0; retry < maxRetries; retry++) {
            try {
                EC2Instance instance = longPollInstanceStatus(instanceId, 30);
                System.out.println("Long poll result: Status = " + instance.getStatus());

                if (instance.getStatus() == EC2Instance.InstanceStatus.RUNNING) {
                    System.out.println("Instance is running!");
                    break;
                }

                // If still pending after timeout, retry
                System.out.println("Still pending after timeout, retrying...");

            } catch (Exception e) {
                System.out.println("Long poll failed, retry " + (retry + 1) + "/" + maxRetries + ": " + e.getMessage());
                if (retry == maxRetries - 1) {
                    throw e;
                }
                Thread.sleep(1000); // Brief pause before retry
            }
        }
    }
}