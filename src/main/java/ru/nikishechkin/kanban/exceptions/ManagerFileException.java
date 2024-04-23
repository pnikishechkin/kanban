package ru.nikishechkin.kanban.exceptions;

public class ManagerFileException extends RuntimeException {

    public ManagerFileException(String message) {
        super(message);
    }

    public ManagerFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
