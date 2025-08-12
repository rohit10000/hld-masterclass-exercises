package org.hertever;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    
    private static final int THREAD_COUNT = 10;
    
    public static void main(String[] args) {
        System.out.println("Starting client with " + THREAD_COUNT + " concurrent threads");
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i + 1;
            executor.submit(() -> {
                try {
                    System.out.println("Thread " + threadId + " starting order...");
                    Order order = new Order();
                    boolean result = order.order();
                    
                    if (result) {
                        successCount.incrementAndGet();
                        System.out.println("Thread " + threadId + " - Order SUCCESS");
                    } else {
                        failureCount.incrementAndGet();
                        System.out.println("Thread " + threadId + " - Order FAILED");
                    }
                    
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.err.println("Thread " + threadId + " - Exception occurred: " + e.getMessage());
                }
            });
        }
        
        executor.shutdown();
        
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("Timeout waiting for threads to complete");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for threads to complete");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            executor.close();
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("\n=== EXECUTION SUMMARY ===");
        System.out.println("Total threads: " + THREAD_COUNT);
        System.out.println("Successful orders: " + successCount.get());
        System.out.println("Failed orders: " + failureCount.get());
        System.out.println("Success rate: " + String.format("%.2f%%", (successCount.get() * 100.0) / THREAD_COUNT));
        System.out.println("Total execution time: " + totalTime + "ms");
        System.out.println("Average time per thread: " + String.format("%.2f ms", totalTime / (double) THREAD_COUNT));
    }
}
