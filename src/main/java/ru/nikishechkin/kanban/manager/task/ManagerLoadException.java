package ru.nikishechkin.kanban.manager.task;

public class ManagerLoadException extends RuntimeException{

    public ManagerLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
