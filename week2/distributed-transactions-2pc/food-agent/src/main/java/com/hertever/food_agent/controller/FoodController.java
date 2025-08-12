package com.hertever.food_agent.controller;

import com.hertever.food_agent.service.FoodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/food")
public class FoodController {

    private static final Logger logger = LoggerFactory.getLogger(FoodController.class);

    @Autowired
    private FoodService foodService;
    
    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@RequestParam Integer foodId) {
        logger.info("Received request to reserve food packet for foodId: {}", foodId);
        
        try {
            boolean success = foodService.reserve(foodId);
            if (success) {
                logger.info("Food packet reserved successfully for foodId: {}", foodId);
                return ResponseEntity.ok("Food packet reserved successfully");
            } else {
                logger.warn("Failed to reserve food packet for foodId: {} - no available packet found", foodId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("No available food packet found");
            }
        } catch (Exception e) {
            logger.error("Error occurred while reserving food packet for foodId: {}", foodId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
    
    @PostMapping("/book")
    public ResponseEntity<String> book(@RequestParam String orderId, @RequestParam Integer foodId) {
        logger.info("Received request to book food packet for orderId: {} and foodId: {}", orderId, foodId);
        
        try {
            boolean success = foodService.book(orderId, foodId);
            if (success) {
                logger.info("Food packet booked successfully for orderId: {} and foodId: {}", orderId, foodId);
                return ResponseEntity.ok("Food packet booked successfully");
            } else {
                logger.warn("Failed to book food packet for orderId: {} and foodId: {} - no reserved packet found", orderId, foodId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("No reserved food packet found");
            }
        } catch (Exception e) {
            logger.error("Error occurred while booking food packet for orderId: {} and foodId: {}", orderId, foodId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
