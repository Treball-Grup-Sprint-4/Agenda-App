package com.agenda.event.service;

import com.agenda.event.dto.EventDto;

public interface EventObserver {

    void notify(EventDto event);
}