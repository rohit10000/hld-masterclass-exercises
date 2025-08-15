package org.example.mutex;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class PrinterResource {
    // Binary semaphore (permits = 1) acts as a mutex
    private final Semaphore mutex = new Semaphore(1, true); // fair=true for FIFO ordering
    private int jobCounter = 0;

    public void printJob(String jobName) {
        try {
            System.out.println(Thread.currentThread().getName() +
                    " requesting printer for job: " + jobName);

            mutex.acquire(); // Acquire the mutex (blocking)

            try {
                // Critical section - only one thread can be here
                jobCounter++;
                System.out.println(Thread.currentThread().getName() +
                        " acquired printer, printing job #" + jobCounter +
                        ": " + jobName);

                // Simulate printing time
                Thread.sleep(200);

                System.out.println(Thread.currentThread().getName() +
                        " finished printing job: " + jobName);

            } finally {
                mutex.release(); // Always release the mutex
                System.out.println(Thread.currentThread().getName() +
                        " released printer");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " interrupted");
        }
    }

    public boolean tryPrintJob(String jobName, long timeoutMs) {
        try {
            System.out.println(Thread.currentThread().getName() +
                    " trying to get printer for job: " + jobName +
                    " (timeout: " + timeoutMs + "ms)");

            if (mutex.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
                try {
                    jobCounter++;
                    System.out.println(Thread.currentThread().getName() +
                            " got printer immediately, printing job #" +
                            jobCounter + ": " + jobName);

                    Thread.sleep(150);
                    System.out.println(Thread.currentThread().getName() +
                            " finished printing job: " + jobName);
                    return true;

                } finally {
                    mutex.release();
                    System.out.println(Thread.currentThread().getName() +
                            " released printer");
                }
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " could not get printer within timeout for job: " + jobName);
                return false;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void printQueueInfo() {
        System.out.println("Available permits: " + mutex.availablePermits());
        System.out.println("Queued threads: " + mutex.getQueueLength());
        System.out.println("Has queued threads: " + mutex.hasQueuedThreads());
    }

    public int getJobCounter() {
        return jobCounter;
    }
}

public class SemaphoreBinaryMutexExample {
    public static void main(String[] args) {
        PrinterResource printer = new PrinterResource();

        // Create multiple print jobs
        Thread[] printJobs = new Thread[5];

        for (int i = 0; i < printJobs.length; i++) {
            final int jobId = i;
            final String jobName = "Document-" + jobId;

            printJobs[i] = new Thread(() -> {
                if (jobId % 3 == 0) {
                    // Every 3rd job uses blocking acquire
                    printer.printJob(jobName);
                } else {
                    // Other jobs use tryAcquire with timeout
                    boolean success = printer.tryPrintJob(jobName, 100);
                    if (!success) {
                        System.out.println(Thread.currentThread().getName() +
                                " giving up on job: " + jobName);
                    }
                }
            }, "PrintThread-" + i);
        }

        // Start all print jobs
        for (Thread job : printJobs) {
            job.start();
        }

        // Show queue info after a short delay
        try {
            Thread.sleep(50);
            System.out.println("\n--- Printer Queue Status ---");
            printer.printQueueInfo();
            System.out.println("---------------------------\n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for all jobs to complete
        for (Thread job : printJobs) {
            try {
                job.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\nAll print jobs completed!");
        System.out.println("Total jobs printed: " + printer.getJobCounter());
    }
}