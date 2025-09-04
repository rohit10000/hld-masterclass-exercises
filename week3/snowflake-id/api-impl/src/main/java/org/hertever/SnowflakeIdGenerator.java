package org.hertever;

import java.time.Instant;

public class SnowflakeIdGenerator {
    private final long MAX_MACHINE_ID = (1 << 10) - 1;
    private final long MAX_SEQUENCE_ID = (1 << 12) - 1;
    private final int MACHINE_SHIFT = 10;
    private final int SEQUENCE_SHIFT = 12;
    private final int TIMESTAMP_SHIFT = MACHINE_SHIFT + SEQUENCE_SHIFT;
    private final long machineId;
    private long lastTimestamp = 0L;
    private long sequenceId = 0L;

    public SnowflakeIdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new RuntimeException("Invalid machine id");
        }
        this.machineId = machineId;
    }

    public long nextId() {
        long timestamp = Instant.now().getEpochSecond();
//        if (lastTimestamp > timestamp) {
//             throw new RuntimeException("Back in time timestamp. Cannot generate id");
//        }
        System.out.println(timestamp);
        if (timestamp <= lastTimestamp) {
            sequenceId = (sequenceId + 1) % MAX_SEQUENCE_ID;
            if (sequenceId == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequenceId = 0;
        }
        lastTimestamp = timestamp;

        return (timestamp << TIMESTAMP_SHIFT) | (machineId << SEQUENCE_SHIFT) | sequenceId;
    }

    public void parse(long snowflakeId) {
        long timestamp = snowflakeId >> TIMESTAMP_SHIFT;
        long machineId = (snowflakeId >> SEQUENCE_SHIFT) & MAX_MACHINE_ID;
        long sequenceId = snowflakeId & MAX_SEQUENCE_ID;
        System.out.printf("Timestamp: %d, machineId: %d and sequenceId: %d\n", timestamp, machineId, sequenceId);
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
