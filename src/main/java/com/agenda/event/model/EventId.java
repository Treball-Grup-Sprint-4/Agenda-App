package com.agenda.event.model;

public record EventId(int value) {

    public EventId {
        if (value <= 0) {
            throw new IllegalArgumentException("Event ID must be greater than 0");
        }
    }
}
