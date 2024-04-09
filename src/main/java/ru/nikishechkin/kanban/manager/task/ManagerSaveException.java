package ru.nikishechkin.kanban.manager.task;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
