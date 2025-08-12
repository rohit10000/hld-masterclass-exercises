# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application implementing a **2-Phase Commit (2PC) distributed transaction system** for food ordering. The system manages reservations and bookings for both food items and delivery agents using a two-phase commit protocol.

## Architecture

### Core Components
- **FoodController**: Handles `/food/reserve` and `/food/book` endpoints
- **AgentController**: Handles `/agent/reserve` and `/agent/book` endpoints (to be implemented)
- **FoodService**: Manages food packet reservations and bookings with SQL transactions
- **AgentService**: Manages agent reservations and bookings with SQL transactions (to be implemented)

### Database Schema
- **agents**: `id | is_reserved | order_id`
- **packets**: `id | food_id (FK) | is_reserved | order_id`  
- **foods**: `id | name`

### 2-Phase Commit Flow
1. **Reserve Phase**: Reserve both agent and food packet (set `is_reserved = true`)
2. **Commit Phase**: Book both agent and food packet (set `order_id`, `is_reserved = false`)

Each operation uses SQL transactions with `SELECT ... FOR UPDATE` for row-level locking.

## Development Commands

### Build and Run
```bash
./gradlew build          # Build the project
./gradlew bootRun        # Run the Spring Boot application
./gradlew test           # Run tests
```

### Database Configuration
- **Database**: MySQL
- **Host**: localhost:3306
- **User**: root
- **Password**: rohitsingh  
- **Database**: hertever

## Key Implementation Notes

- All service methods use explicit SQL transaction management (`begin()`, `commit()`, `rollback()`)
- Row-level locking with `SELECT ... FOR UPDATE` prevents race conditions
- Services implement the reserve-then-book pattern for 2PC
- Repository layer should be added for database operations
- Client performs serial operations: reserve agent → reserve food → book agent → book food

## Technology Stack

- **Framework**: Spring Boot 3.5.4
- **Java Version**: 24
- **Build Tool**: Gradle
- **Database**: MySQL
- **Additional Dependencies**: Lombok for boilerplate code reduction