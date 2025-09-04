package org.hertever;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String machineIdStr = System.getenv("MACHINE_ID");
        long machineId = machineIdStr != null ? Long.parseLong(machineIdStr) : 0L;
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(machineId);
        for (int i = 0; i < 10; i++) {
            long snowflakeId = generator.nextId();
            generator.parse(snowflakeId);
        }
    }
}