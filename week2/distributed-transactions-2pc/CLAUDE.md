# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This project implements a **Two-Phase Commit (2PC) distributed transaction system** for food ordering using Spring Boot. The system consists of two main components:

1. **food-agent**: Spring Boot service managing food packets and delivery agents
2. **order-client**: Client application that orchestrates the 2PC protocol

## Architecture

### Two-Phase Commit Flow
The system implements 2PC with these phases:
1. **Reserve Phase**: Reserve both agent and food packet (sets `is_reserved = true`)
2. **Commit Phase**: Book both agent and food packet (sets `order_id`, `is_reserved = false`)

### Core Components

**food-agent Service:**
- **Controllers**: `FoodController`, `AgentController` - REST endpoints for reserve/book operations
- **Services**: `AgentService`, `FoodService` - Business logic with transaction management
- **Repositories**: `AgentRepository`, `PacketRepository` - Database operations with row-level locking
- **TransactionManager**: Custom transaction utility with connection management and logging

**order-client:**
- **Client**: Main entry point spawning 10 concurrent threads
- **Order**: Implements the 2PC orchestration logic

### Database Schema
- **agents**: `id | is_reserved | order_id`
- **packets**: `id | food_id (FK) | is_reserved | order_id`
- **foods**: `id | name`

## Development Commands

### food-agent (Spring Boot Service)
```bash
cd food-agent
./gradlew bootRun        # Start the Spring Boot service
./gradlew build          # Build the project
./gradlew test           # Run tests
```

### order-client
```bash
cd order-client
./gradlew build          # Build the client
./gradlew run            # Run the client (if configured)
java -cp build/libs/order-client-1.0-SNAPSHOT.jar org.hertever.Client  # Manual execution
```

### Database Configuration
- **Database**: MySQL
- **Connection**: localhost:3306/hertever
- **Credentials**: root/rohitsingh
- **Driver**: MySQL Connector/J

## Key Implementation Details

### Transaction Management
- All database operations use the custom `TransactionManager` utility
- Row-level locking with `SELECT ... FOR UPDATE` prevents race conditions
- Explicit transaction boundaries with proper rollback handling
- Comprehensive logging at all transaction stages

### 2PC Protocol Implementation
Each order follows this sequence:
1. `POST /agent/reserve` - Reserve an available agent
2. `POST /food/reserve` - Reserve a food packet
3. `POST /agent/book` - Commit agent reservation with order ID
4. `POST /food/book` - Commit food packet reservation with order ID

Failure at any step triggers rollback of the entire transaction.

### Concurrency Handling
- Client spawns 10 concurrent threads to test system under load
- Database-level locking ensures atomicity across concurrent requests
- Services return boolean success/failure for clear transaction outcomes

## Technology Stack

**food-agent:**
- Spring Boot 3.5.4
- Java 24
- MySQL Connector/J
- Lombok
- Gradle build system

**order-client:**
- Plain Java application
- JUnit 5 for testing
- Gradle build system