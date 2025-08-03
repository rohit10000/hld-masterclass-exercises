package com.example.long_short_polling.client;

// ShortPollingClient.java - Client implementation for short polling
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;

import com.example.long_short_polling.model.EC2Instance;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShortPollingClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ShortPollingClient(String baseUrl) {
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

    public EC2Instance pollInstanceStatus(String instanceId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/ec2/status/" + instanceId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), EC2Instance.class);
    }

    // Short polling with retry logic
    public void waitForInstanceRunning(String instanceId, int maxAttempts) throws Exception {
        System.out.println("Starting short polling for instance: " + instanceId);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            EC2Instance instance = pollInstanceStatus(instanceId);
            System.out.println("Attempt " + attempt + ": Status = " + instance.getStatus());

            if (instance.getStatus() == EC2Instance.InstanceStatus.RUNNING) {
                System.out.println("Instance is running after " + attempt + " attempts");
                return;
            }

            if (attempt < maxAttempts) {
                Thread.sleep(2000); // Wait 2 seconds between polls
            }
        }

        throw new RuntimeException("Instance did not become running after " + maxAttempts + " attempts");
    }
}
