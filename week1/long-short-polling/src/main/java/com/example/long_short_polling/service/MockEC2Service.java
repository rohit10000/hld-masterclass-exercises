package com.example.long_short_polling.service;

// MockEC2Service.java - Service to simulate EC2 creation
import com.example.long_short_polling.model.EC2Instance;

import java.util.concurrent.*;
import java.util.UUID;
import java.util.Map;

public class MockEC2Service {
    private final Map<String, EC2Instance> instances = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public String createInstance() {
        String instanceId = "i-" + UUID.randomUUID().toString().substring(0, 8);
        EC2Instance instance = new EC2Instance(instanceId);
        instances.put(instanceId, instance);

        // Simulate EC2 creation process asynchronously
        executor.submit(() -> {
            try {
                // Simulate creation time (10-20 seconds)
                Thread.sleep(10000 + (long)(Math.random() * 10000));
                instance.setStatus(EC2Instance.InstanceStatus.RUNNING);
                System.out.println("Instance " + instanceId + " is now RUNNING");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        return instanceId;
    }

    public EC2Instance getInstance(String instanceId) {
        return instances.get(instanceId);
    }

    public void shutdown() {
        executor.shutdown();
    }
}

