# Server-Sent Events (SSE) Server

A simple Server-Sent Events (SSE) server built with Java and Spring Boot that demonstrates real-time event streaming to web clients.

## Features

- **Real-time Event Streaming**: Multiple SSE endpoints for different types of events
- **Multiple Event Types**: Welcome messages, heartbeats, time updates, and broadcast messages
- **Client Management**: Automatic handling of client connections and disconnections
- **Web Interface**: Built-in HTML client for testing
- **Cross-Origin Support**: CORS enabled for development

## Prerequisites

- Java 17 or higher
- No need to install Gradle (uses Gradle wrapper)

## Project Structure

```
sse/
├── build.gradle                 # Gradle build configuration
├── settings.gradle              # Gradle settings
├── gradlew                      # Gradle wrapper script (Unix)
├── gradlew.bat                 # Gradle wrapper script (Windows)
├── gradle/wrapper/             # Gradle wrapper files
├── src/main/java/com/example/sse/
│   ├── SseServerApplication.java    # Main Spring Boot application
│   └── SseController.java          # SSE endpoints controller
├── src/main/resources/
│   ├── application.properties      # Application configuration
│   └── static/index.html          # Test client web page
└── README.md                   # This file
```

## Available Endpoints

### SSE Endpoints

1. **`GET /events`** - Main event stream
   - Sends welcome message on connection
   - Receives periodic heartbeat messages (every 5 seconds)
   - Receives broadcast messages from other clients

2. **`GET /time`** - Time stream
   - Sends current timestamp every second

### REST Endpoints

3. **`POST /broadcast`** - Send broadcast message
   - Body: `{"message": "your message here"}`
   - Broadcasts message to all connected `/events` clients

4. **`GET /status`** - Server status
   - Returns current status and number of connected clients

5. **`GET /`** - Test client
   - Serves the HTML test client interface

## Running the Server

### Option 1: Using Gradle Wrapper (Recommended)

```bash
# On Unix/macOS/Linux
./gradlew bootRun

# On Windows
gradlew.bat bootRun
```

### Option 2: Build and Run JAR

```bash
# Build the JAR
./gradlew build

# Run the JAR
java -jar build/libs/sse-server-0.0.1-SNAPSHOT.jar
```

## Testing the Server

### Using the Web Interface

1. Start the server (it runs on port 8080 by default)
2. Open your browser and navigate to: `http://localhost:8080`
3. Use the web interface to:
   - Connect to event streams
   - Send broadcast messages
   - Monitor real-time events

### Using curl

Test the endpoints with curl:

```bash
# Check server status
curl http://localhost:8080/status

# Connect to events stream (will keep connection open)
curl -N http://localhost:8080/events

# Connect to time stream
curl -N http://localhost:8080/time

# Send broadcast message
curl -X POST http://localhost:8080/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello from curl!"}'
```

### Using JavaScript (EventSource)

```javascript
// Connect to events stream
const eventSource = new EventSource('http://localhost:8080/events');

eventSource.onmessage = function(event) {
    console.log('Received:', event.data);
};

eventSource.addEventListener('heartbeat', function(event) {
    console.log('Heartbeat:', event.data);
});

eventSource.addEventListener('broadcast', function(event) {
    console.log('Broadcast:', event.data);
});
```

## Event Types

The server sends different types of events:

- **`message`**: Welcome messages when clients connect
- **`heartbeat`**: Periodic server heartbeat (every 5 seconds)
- **`broadcast`**: Messages sent via the `/broadcast` endpoint
- **`time`**: Timestamp updates (only on `/time` endpoint)

## Configuration

You can modify the server configuration in `src/main/resources/application.properties`:

```properties
# Change server port
server.port=8080

# Application name
spring.application.name=sse-server

# CORS configuration
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
```

## Development

### Adding New Event Types

1. Add a new endpoint in `SseController.java`
2. Use `SseEmitter.event().name("your-event-type").data("your-data")`
3. Update the HTML client to handle the new event type

### Customizing Update Intervals

Modify the `ScheduledExecutorService` intervals in `SseController.java`:

```java
// Change heartbeat interval (currently 5 seconds)
executor.scheduleAtFixedRate(this::sendPeriodicUpdate, 0, 5, TimeUnit.SECONDS);

// Change time update interval (currently 1 second)
timeExecutor.scheduleAtFixedRate(() -> { ... }, 0, 1, TimeUnit.SECONDS);
```

## Production Considerations

- **Security**: Remove `@CrossOrigin(origins = "*")` and configure proper CORS
- **Authentication**: Add authentication/authorization as needed
- **Rate Limiting**: Implement rate limiting for broadcast endpoints
- **Connection Limits**: Consider limiting the number of concurrent connections
- **Monitoring**: Add proper logging and monitoring
- **Load Balancing**: For multiple instances, consider sticky sessions

## Troubleshooting

### Port Already in Use
If port 8080 is already in use, change it in `application.properties` or run with:
```bash
./gradlew bootRun --args='--server.port=8081'
```

### Connection Issues
- Check firewall settings
- Ensure CORS is properly configured for your domain
- Verify the server is running and accessible

### Build Issues
- Ensure Java 17+ is installed and `JAVA_HOME` is set
- Try `./gradlew clean build` to clean and rebuild

## License

This project is for educational purposes and demonstrations. 