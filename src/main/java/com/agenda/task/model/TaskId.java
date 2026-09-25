package com.agenda.task.model;

public record TaskId(int value) {

    public TaskId {
        if (value <= 0) {
            throw new IllegalArgumentException("Task ID must be greater than 0");
        }
    }
}
