package com.hertever.food_agent.controller;

import com.hertever.food_agent.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;
    
    @PostMapping("/reserve")
    public ResponseEntity<String> reserve() {
        logger.info("Received request to reserve agent");
        
        try {
            boolean success = agentService.reserve();
            if (success) {
                logger.info("Agent reserved successfully");
                return ResponseEntity.ok("Agent reserved successfully");
            } else {
                logger.warn("Failed to reserve agent - no available agent found");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("No available agent found");
            }
        } catch (Exception e) {
            logger.error("Error occurred while reserving agent", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
    
    @PostMapping("/book")
    public ResponseEntity<String> book(@RequestParam String orderId) {
        logger.info("Received request to book agent for orderId: {}", orderId);
        
        try {
            boolean success = agentService.book(orderId);
            if (success) {
                logger.info("Agent booked successfully for orderId: {}", orderId);
                return ResponseEntity.ok("Agent booked successfully");
            } else {
                logger.warn("Failed to book agent for orderId: {} - no reserved agent found", orderId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("No reserved agent found");
            }
        } catch (Exception e) {
            logger.error("Error occurred while booking agent for orderId: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}