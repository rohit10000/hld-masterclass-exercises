package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.example.consumer.SimpleKafkaConsumer;
import org.example.es.ElasticSearchManager;

public class Main {
    public static void main(String[] args) {
        SimpleKafkaConsumer simpleKafkaConsumer = new SimpleKafkaConsumer();
        ElasticSearchManager elasticSearchManager = new ElasticSearchManager();

        while (true) {
            try {
                ConsumerRecords<String, String> records = simpleKafkaConsumer.poll();
                System.out.println("polling ...");
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Consumed message: key=%s, value=%s, partition=%d, offset=%d%n",
                            record.key(), record.value(), record.partition(), record.offset());
                    String value = record.value();
                    elasticSearchManager.putData(value, "my_index");

                }
            } finally {
                simpleKafkaConsumer.close();
                elasticSearchManager.close();
            }
        }
    }
}