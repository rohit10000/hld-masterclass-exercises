package com.example.long_short_polling;

import com.example.long_short_polling.client.LongPollingClient;
import com.example.long_short_polling.client.ShortPollingClient;
import com.example.long_short_polling.service.MockEC2Service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LongShortPollingApplication {

	@Bean
	public MockEC2Service mockEC2Service() {
		return new MockEC2Service();
	}

	public static void main(String[] args) {
		SpringApplication.run(LongShortPollingApplication.class, args);

		// Demo both polling approaches
		demonstratePolling();
	}

	private static void demonstratePolling() {
		try {
			String baseUrl = "http://localhost:8080";

			System.out.println("=== SHORT POLLING DEMO ===");
			ShortPollingClient shortClient = new ShortPollingClient(baseUrl);
			String instanceId1 = shortClient.createInstance();
			System.out.println("Created instance: " + instanceId1);

			// Start short polling in separate thread
			new Thread(() -> {
				try {
					shortClient.waitForInstanceRunning(instanceId1, 20);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();

			Thread.sleep(5000); // Wait a bit before starting long polling demo

			System.out.println("\n=== LONG POLLING DEMO ===");
			LongPollingClient longClient = new LongPollingClient(baseUrl);
			String instanceId2 = longClient.createInstance();
			System.out.println("Created instance: " + instanceId2);

			// Start long polling in separate thread
			new Thread(() -> {
				try {
					longClient.waitForInstanceRunning(instanceId2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
