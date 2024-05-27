package ru.nikishechkin.kanban.exceptions;

public class OverlapException extends RuntimeException {

    public OverlapException(String message) {
        super(message);
    }

    public OverlapException(String message, Throwable cause) {
        super(message, cause);
    }
}
