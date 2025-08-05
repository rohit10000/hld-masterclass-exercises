package com.example.long_short_polling;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

enum PollingType {
    SHORT,
    LONG
}

enum Status {
    PROVISIONING,
    RUNNING
}

@Getter
class Ec2Instance {

    private final String name;
    private Status status;
    private static final AtomicInteger id = new AtomicInteger(0);
    private final ArrayList<Function<String, String>> callbacks = new ArrayList<>();

    private Ec2Instance(String name) {
        this.name = name;
    }

    public static Ec2Instance createInstance() {
        Ec2Instance instance = new Ec2Instance("instance " + id.toString());
        id.incrementAndGet();
        instance.status = Status.PROVISIONING;
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            instance.status = Status.RUNNING;
            for (Function<String, String> callback: instance.getCallbacks()) {
                String message = callback.apply(instance.getName());
                System.out.println(message);
            }
        });
        thread.start();
        return instance;
    }

    public void registerCallback(Function<String, String> callback) {
        callbacks.add(callback);
    }
}

//Simple work around example for this.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Type 0 for short polling or otherwise for long polling: ");
        int type = sc.nextInt();
        switch (type == 0 ? PollingType.SHORT : PollingType.LONG) {
            case SHORT -> {
                Ec2Instance instance = Ec2Instance.createInstance();
                int maxRetry = 100;
                for (int i = 0; i < maxRetry; i++) {
                    System.out.println("Short polling: polled...");
                    Thread.sleep(2000);
                    if (instance.getStatus().equals(Status.RUNNING)) {
                        System.out.printf("Short Polling: Instance %s is running now. \n", instance.getName());
                        return;
                    }
                }
            }
            case LONG -> {
                Ec2Instance instance = Ec2Instance.createInstance();
                System.out.println("Long polled...");
                instance.registerCallback(
                        (name) -> "Long polling: Instance " + name + " is running now."
                );


            }
            default -> System.out.println("Invalid choice.");

        }
    }
}
