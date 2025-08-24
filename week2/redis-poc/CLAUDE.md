# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is a Spring Boot application demonstrating Redis integration using Spring Data Redis and the Lettuce connector. The application is a proof-of-concept that connects to Redis, performs basic key-value operations, and runs as a CommandLineRunner.

## Build and Development Commands

### Building the Project
```bash
./gradlew build
```

### Running the Application
```bash
./gradlew bootRun
```

### Running Tests
```bash
./gradlew test
```

### Running a Single Test
```bash
./gradlew test --tests "RedisPocApplicationTests.contextLoads"
```

### Clean Build
```bash
./gradlew clean build
```

## Architecture and Structure

### Key Components
- **RedisPocApplication.java**: Main application class that implements CommandLineRunner to demonstrate Redis operations on startup
- Uses **Lettuce** as the Redis client (Spring Boot default)
- **RedisTemplate** configured with StringRedisSerializer for both keys and values
- Basic Redis operations demonstrated: set/get key-value pairs

### Dependencies
- Spring Boot 3.5.5 with Java 21
- Spring Data Redis (with Lettuce connector)
- Spring Web (for potential REST endpoints)
- Lombok for reducing boilerplate code
- JUnit 5 for testing

### Configuration
- Minimal configuration in `application.properties`
- Redis connection uses default localhost:6379 (configured via LettuceConnectionFactory)
- Manual connection factory setup in the main application class

## Development Notes
- The application expects a Redis server running on localhost:6379
- Connection factory is manually created and destroyed in the CommandLineRunner
- Uses Apache Commons Logging for output