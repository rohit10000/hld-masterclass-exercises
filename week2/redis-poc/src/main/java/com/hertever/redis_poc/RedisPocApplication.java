package com.hertever.redis_poc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class RedisPocApplication implements CommandLineRunner {

	private static final Log LOG = LogFactory.getLog(RedisPocApplication.class);

	@Autowired LettuceConnectionFactory connectionFactory;

	public static void main(String[] args) {
		SpringApplication.run(RedisPocApplication.class, args);
	}

	@Override
	public void run(String... args) throws InterruptedException {
		connectionFactory.afterPropertiesSet();

		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setDefaultSerializer(StringRedisSerializer.UTF_8);
		template.afterPropertiesSet();

		template.opsForValue().set("foo", "bar");
		LOG.info("Value at foo: " + template.opsForValue().get("foo"));
	}
}


