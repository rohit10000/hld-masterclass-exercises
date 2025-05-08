import exception.CustomConcurrencyException;

import java.sql.Time;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.*;

public class Client {
    private static long usingExecutorLibrary(int n) {
        Instant startTime = Instant.now();
        try (ExecutorService executorService = Executors.newFixedThreadPool(5)) {
            for (int i = 0; i < n; i++) {
                executorService.submit(() -> System.out.print(""));
            }
        }
        Instant endTime = Instant.now();
        return endTime.toEpochMilli() - startTime.toEpochMilli();
    }

    private static long usingCustomThreadPoolExecutor(int n) {
        Instant startTime = Instant.now();
        CustomThreadPoolExecutor customThreadPoolExecutor = new CustomThreadPoolExecutor(5);
        for (int i = 0; i < n; i++) {
            customThreadPoolExecutor.execute(() -> System.out.print(""));
        }
        customThreadPoolExecutor.shutdown();
        Instant endTime = Instant.now();
        return endTime.toEpochMilli() - startTime.toEpochMilli();
    }

    private static long usingThreadCreationEachTime(int n) {
        Instant startTime = Instant.now();
        for (int i = 0; i < n; i++) {
            Thread thread = new Thread(() -> System.out.print(""));
            thread.start();
            try {
                thread.join();
            } catch (Exception ex) {
                throw new CustomConcurrencyException("Thread join failed.");
            }
        }
        Instant endTime = Instant.now();
        return endTime.toEpochMilli() - startTime.toEpochMilli();
    }

    private static void compare(int n) throws InterruptedException {

        System.out.println("============ Executor comparison for size: " + n + " ==============");

        long timeTaken1 = usingExecutorLibrary(n);
        System.out.println("Total time taken 1: " + timeTaken1);

        long timeTaken2 = usingCustomThreadPoolExecutor(n);
        System.out.println("Total time taken 2: " + timeTaken2);

        long timeTaken3 = usingThreadCreationEachTime(n);
        System.out.println("Total time taken 3: " + timeTaken3);

    }
    public static void main(String[] args) throws InterruptedException {
        int[] arr = {500, 5000, 50000, 500000};
        for (int i = 0; i < arr.length; i++) {
            int size = arr[i];
            compare(size);
        }
    }

}
