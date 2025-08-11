package org.hertever;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private Map<String, String> dbMap = new HashMap<>();
    public Database() {
        dbMap.put("apple", "fruit");
        dbMap.put("rainy", "season");
        dbMap.put("red", "color");
        dbMap.put("parrot", "bird");
    }

    public String getValue(String key) throws InterruptedException {
        System.out.println("[Database]     Fetching from db....");
        Thread.sleep(1000);
        String value = dbMap.get(key);
        if (value == null) {
            throw new RuntimeException("Key not found in db");
        }
        System.out.printf("[Database]     %s. Value fetched from db for the key: %s is %s\n",
                Thread.currentThread().getName(), key, value);
        return value;

    }
    
}
