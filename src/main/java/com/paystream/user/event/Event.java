package com.paystream.user.event;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event<T> {
    private UUID eventId;
    private String eventType;
    private int version;
    private String source;
    private Timestamp timestamp;
    private String traceId;
    private T payload;
}

