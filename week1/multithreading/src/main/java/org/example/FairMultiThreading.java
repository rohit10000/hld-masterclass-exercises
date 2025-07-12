package org.example;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FairMultiThreading {
    // Shared atomic counter to count the number of primes found across all threads
    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        int n = 10; // Number of threads in the fixed thread pool

        // Create a fixed thread pool with n threads for parallel prime checking
        ExecutorService executorService = Executors.newFixedThreadPool(n);

        // Latch to wait for all submitted tasks to complete (one for each number checked)
        CountDownLatch latch = new CountDownLatch(9998); // 9998 numbers from 2 to 9999

        // Record the start time for performance measurement
        long startInMillis = Instant.now().toEpochMilli();

        // Submit a task for each number from 2 to 9999 to check if it's prime
        for (int i = 2; i < 10000; i++) {
            int finalI = i;
            executorService.submit(() -> {
                isPrime(finalI); // Check if the number is prime and increment counter if so
                latch.countDown(); // Signal that this task is done
            });
        }

        // Wait for all tasks to finish before proceeding
        latch.await();

        // Shut down the executor service (no new tasks will be accepted)
        executorService.shutdown();

        // Print the total time taken and the number of primes found
        System.out.printf("Time taken: %d, total primes: %d",
                (Instant.now().toEpochMilli() - startInMillis)/1000, counter.get());
    }

    // Checks if a number is prime; increments the counter if it is
    private static void isPrime(int n) {
        // Only check divisibility up to sqrt(n)
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return; // Not a prime number
            }
        }
        counter.incrementAndGet(); // Prime found, increment the counter
    }
}

// Simple single-threaded class for comparison
class SimpleClass {
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        // Record the start time for performance measurement
        long startInMillis = Instant.now().toEpochMilli();

        // Check each number from 2 to 9999 for primality (single-threaded)
        for (int i = 2; i < 10000; i++) {
            isPrime(i);
        }

        // Print the total time taken and the number of primes found
        System.out.printf("Time taken: %d, total primes: %d",
                (Instant.now().toEpochMilli() - startInMillis)/1000, counter.get());
    }

    // Checks if a number is prime; increments the counter if it is
    private static void isPrime(int n) {
        // Only check divisibility up to sqrt(n)
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return; // Not a prime number
            }
        }
        counter.incrementAndGet(); // Prime found, increment the counter
    }
}
