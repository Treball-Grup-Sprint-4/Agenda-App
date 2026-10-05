package com.agenda.common.exception;

public class EmptyDatabaseException extends RuntimeException {

    public EmptyDatabaseException(String message) {
        super(message);
    }
}
