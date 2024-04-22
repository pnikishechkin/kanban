package ru.nikishechkin.kanban.manager;

import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.manager.history.InMemoryHistoryManager;
import ru.nikishechkin.kanban.manager.task.FileBackedTaskManager;
import ru.nikishechkin.kanban.manager.task.InMemoryTaskManager;
import ru.nikishechkin.kanban.manager.task.TaskManager;

public class Managers {

    private static HistoryManager historyManager;
    private static TaskManager taskManager;

    private Managers() { }

    public static TaskManager getDefault() {
        taskManager = new InMemoryTaskManager(getDefaultHistory());
        return taskManager;
    }

    public static FileBackedTaskManager getFileBackedTaskManager(String fileName) {
        return FileBackedTaskManager.loadFromFile(getDefaultHistory(), fileName);
    }

    public static HistoryManager getDefaultHistory() {
        historyManager = new InMemoryHistoryManager();
        return historyManager;
    }

}
