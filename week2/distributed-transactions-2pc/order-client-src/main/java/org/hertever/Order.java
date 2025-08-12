package org.hertever;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Order {
    
    private static final String BASE_URL = "http://localhost:8080";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int FOOD_ID = 1;
    
    private final OkHttpClient client;

    public Order() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }
    
    public boolean order() {
        String orderId = UUID.randomUUID().toString();
        System.out.println("Starting order with ID: " + orderId);
        
        try {
            if (!reserveAgent()) {
                System.err.println("Failed to reserve agent for order: " + orderId);
                return false;
            }
            
            if (!reserveFood(FOOD_ID)) {
                System.err.println("Failed to reserve food for order: " + orderId);
                return false;
            }
            
            if (!bookAgent(orderId)) {
                System.err.println("Failed to book agent for order: " + orderId);
                return false;
            }
            
            if (!bookFood(FOOD_ID, orderId)) {
                System.err.println("Failed to book food for order: " + orderId);
                return false;
            }
            
            System.out.println("Order completed successfully: " + orderId);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error during order processing for ID " + orderId + ": " + e.getMessage());
            return false;
        }
    }
    
    private boolean reserveAgent() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/agent/reserve")
                .post(RequestBody.create("", JSON))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            boolean success = response.isSuccessful();
            System.out.println("Reserve agent response: " + response.code() + " - " + (success ? "SUCCESS" : "FAILED"));
            return success;
        }
    }
    
    private boolean reserveFood(int foodId) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/food/reserve?foodId=" + foodId)
                .post(RequestBody.create("", JSON))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            boolean success = response.isSuccessful();
            System.out.println("Reserve food response: " + response.code() + " - " + (success ? "SUCCESS" : "FAILED"));
            return success;
        }
    }
    
    private boolean bookAgent(String orderId) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/agent/book?orderId=" + orderId)
                .post(RequestBody.create("", JSON))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            boolean success = response.isSuccessful();
            System.out.println("Book agent response: " + response.code() + " - " + (success ? "SUCCESS" : "FAILED"));
            return success;
        }
    }
    
    private boolean bookFood(int foodId, String orderId) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/food/book?foodId=" + foodId + "&orderId=" + orderId)
                .post(RequestBody.create("", JSON))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            boolean success = response.isSuccessful();
            System.out.println("Book food response: " + response.code() + " - " + (success ? "SUCCESS" : "FAILED"));
            return success;
        }
    }
}
