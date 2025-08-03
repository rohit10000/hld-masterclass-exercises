# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application demonstrating long polling vs short polling patterns for monitoring EC2 instance status. The application simulates AWS EC2 instance creation and provides both polling mechanisms to track when instances become RUNNING.

## Architecture

### Core Components

- **MockEC2Service**: Simulates EC2 instance lifecycle with asynchronous state transitions (10-20 second startup time)
- **PollingController**: REST endpoints for both short polling (`/api/ec2/status/{id}`) and long polling (`/api/ec2/status/{id}/long-poll`)
- **Client Implementations**: 
  - `ShortPollingClient`: Polls every 2 seconds with configurable max attempts
  - `LongPollingClient`: Uses server-side waiting with 30-second timeout and retry logic
- **EC2Instance Model**: Represents instance state (PENDING → RUNNING → STOPPED → TERMINATED)

### Key Endpoints

- `POST /api/ec2/create` - Creates new EC2 instance, returns instance ID
- `GET /api/ec2/status/{instanceId}` - Short polling endpoint (immediate response)
- `GET /api/ec2/status/{instanceId}/long-poll?timeoutSeconds=30` - Long polling endpoint (waits for state change)

### Application Flow

The main application automatically demonstrates both polling approaches when started, creating instances and showing the difference in network efficiency between short polling (multiple quick requests) and long polling (single long-held connection).

## Common Development Commands

### Build and Run
```bash
# Build the application
./gradlew build

# Run the application (starts demo automatically)
./gradlew bootRun

# Run tests
./gradlew test
```

### Development
```bash
# Clean build
./gradlew clean build

# Run tests with detailed output
./gradlew test --info

# Build without tests
./gradlew build -x test
```

## Technical Notes

- **Java Version**: 24 with Spring Boot 3.5.4
- **Package Structure**: Uses `com.example.long_short_polling` (note underscore, not hyphen)
- **Concurrency**: MockEC2Service uses CachedThreadPool for asynchronous instance state transitions
- **HTTP Client**: Uses Java 11+ HttpClient for client implementations
- **JSON Handling**: Jackson ObjectMapper for serialization/deserialization

## Testing the Polling Patterns

When the application starts, it automatically demonstrates both patterns:
1. Creates instances via both clients
2. Shows short polling making frequent requests every 2 seconds
3. Shows long polling holding connections until state changes
4. Compare network traffic and responsiveness between approaches