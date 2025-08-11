package org.hertever;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class Cache {
    private final Map<String, String> cacheMap = new ConcurrentHashMap<>();
    private final Map<String, Semaphore> lockMap = new ConcurrentHashMap<>();
    private final Database database = new Database();

    public Cache() {
        cacheMap.put("apple", "fruit");
        cacheMap.put("rainy", "season");
    }

    public void getValue(String key) throws InterruptedException {
        // Fast path - check cache first
        String value = cacheMap.get(key);
        if (value != null) {
            System.out.printf("[Cache   ]     %s. Found for the key: %s value is %s\n",
                    Thread.currentThread().getName(), key, value);
            return;
        }

        // Create or get semaphore for this key (acts as a lock)
        Semaphore semaphore = lockMap.computeIfAbsent(key, k -> new Semaphore(1));

        // Try to acquire the lock
        boolean isLoader = semaphore.tryAcquire();

        if (isLoader) {
            try {
                // Double-check after acquiring lock
                value = cacheMap.get(key);
                if (value != null) {
                    return;
                }

                // I'm the loader thread - fetch from database
                System.out.printf("[Cache   ]     %s. Cache miss, loading from DB for key: %s\n",
                        Thread.currentThread().getName(), key);
                value = database.getValue(key);
                cacheMap.put(key, value);
                System.out.printf("[Cache   ]     %s. Added to cache for key: %s\n",
                        Thread.currentThread().getName(), key);
            } finally {
                semaphore.release();
                // Clean up the lock map to prevent memory leak
                lockMap.remove(key);
            }
        } else {
            // Another thread is loading, wait for it
            System.out.printf("[Cache   ]     %s. Waiting for another thread to load key: %s\n",
                    Thread.currentThread().getName(), key);
            semaphore.acquire();
            semaphore.release(); // Immediately release since we just wanted to wait

            // Value should now be in cache
            value = cacheMap.get(key);
            if (value == null) {
                throw new RuntimeException("Value still not in cache after waiting");
            }
        }
    }
}