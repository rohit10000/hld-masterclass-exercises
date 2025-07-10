package com.example.sse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin(origins = "*")
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public SseController() {
        // Send periodic updates every 5 seconds
        executor.scheduleAtFixedRate(this::sendPeriodicUpdate, 0, 5, TimeUnit.SECONDS);
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        // Add emitter to the list
        emitters.add(emitter);
        
        // Remove emitter when connection is closed
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((ex) -> emitters.remove(emitter));
        
        // Send initial welcome message
        try {
            emitter.send(SseEmitter.event()
                    .id("welcome")
                    .name("message")
                    .data("Connected to SSE stream at " + getCurrentTimestamp()));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @GetMapping(value = "/time", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTime() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        // Send time updates every second
        ScheduledExecutorService timeExecutor = Executors.newSingleThreadScheduledExecutor();
        timeExecutor.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .name("time")
                        .data(getCurrentTimestamp()));
            } catch (IOException e) {
                emitter.completeWithError(e);
                timeExecutor.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        emitter.onCompletion(timeExecutor::shutdown);
        emitter.onTimeout(timeExecutor::shutdown);
        emitter.onError((ex) -> timeExecutor.shutdown());
        
        return emitter;
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcastMessage(@RequestBody MessageRequest request) {
        String message = request.getMessage();
        
        // Send message to all connected clients
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .name("broadcast")
                        .data("Broadcast: " + message + " at " + getCurrentTimestamp()));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        });
        
        return ResponseEntity.ok("Message broadcasted to " + emitters.size() + " clients");
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("SSE Server is running. Connected clients: " + emitters.size());
    }

    private void sendPeriodicUpdate() {
        if (!emitters.isEmpty()) {
            emitters.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .id(String.valueOf(System.currentTimeMillis()))
                            .name("heartbeat")
                            .data("Server heartbeat at " + getCurrentTimestamp()));
                } catch (IOException e) {
                    emitters.remove(emitter);
                }
            });
        }
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Inner class for message request
    public static class MessageRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
} 