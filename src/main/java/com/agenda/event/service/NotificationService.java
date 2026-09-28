package com.agenda.event.service;

import com.agenda.event.dto.EventDto;

public class NotificationService implements EventObserver {

    @Override
    public void notify(EventDto event) {
        System.out.println("Upcoming event: " + event.text() + " - " + event.eventDate());
    }
}
