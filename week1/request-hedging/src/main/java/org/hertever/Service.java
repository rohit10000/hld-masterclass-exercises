package org.hertever;

import java.util.Scanner;

public class Service {
    public static void main(String[] args) {
        System.out.println("Demo for request hedging");
        Scanner sc = new Scanner(System.in);
        Cache cache = new Cache();
        System.out.print("Enter the item to get its category: ");
        String key = sc.nextLine();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    cache.getValue(key);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        }
        sc.close();
    }
}