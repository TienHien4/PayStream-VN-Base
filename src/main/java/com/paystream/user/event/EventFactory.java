package com.paystream.user.event;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

@Component
public class EventFactory {

    public static final EventFactory INSTANCE = new EventFactory();

    public <T> Event<T> createEvent(String eventType, String traceId, T payload) {
        return Event.<T>builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .version(1)
                .source("user-service")
                .traceId(traceId)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .payload(payload)
                .build();
    }
}
