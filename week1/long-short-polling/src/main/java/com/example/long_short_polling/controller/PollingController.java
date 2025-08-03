package com.example.long_short_polling.controller;

// PollingController.java - REST Controller for polling APIs
import com.example.long_short_polling.model.EC2Instance;
import com.example.long_short_polling.service.MockEC2Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/ec2")
public class PollingController {

    @Autowired
    private MockEC2Service ec2Service;

    // Create EC2 instance
    @PostMapping("/create")
    public ResponseEntity<String> createInstance() {
        String instanceId = ec2Service.createInstance();
        return ResponseEntity.ok(instanceId);
    }

    // Short Polling API - Returns current status immediately
    @GetMapping("/status/{instanceId}")
    public ResponseEntity<EC2Instance> getInstanceStatus(@PathVariable String instanceId) {
        EC2Instance instance = ec2Service.getInstance(instanceId);

        if (instance == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(instance);
    }

    // Long Polling API - Waits until status changes or timeout
    @GetMapping("/status/{instanceId}/long-poll")
    public ResponseEntity<EC2Instance> longPollInstanceStatus(
            @PathVariable String instanceId,
            @RequestParam(defaultValue = "30") int timeoutSeconds) {

        EC2Instance instance = ec2Service.getInstance(instanceId);

        if (instance == null) {
            return ResponseEntity.notFound().build();
        }

        // If already running, return immediately
        if (instance.getStatus() == EC2Instance.InstanceStatus.RUNNING) {
            return ResponseEntity.ok(instance);
        }

        // Long polling - wait for status change
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            instance = ec2Service.getInstance(instanceId);

            if (instance.getStatus() == EC2Instance.InstanceStatus.RUNNING) {
                return ResponseEntity.ok(instance);
            }

            try {
                // Poll every 500ms
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Return current status even if timeout occurred
        return ResponseEntity.ok(instance);
    }
}

