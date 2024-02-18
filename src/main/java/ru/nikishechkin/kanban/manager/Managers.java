package ru.nikishechkin.kanban.manager;

public class Managers {

    private static HistoryManager historyManager;
    private static TaskManager taskManager;

    public static TaskManager getDefault() {

        if(historyManager == null) {
            getDefaultHistory();
        }
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager(historyManager);
        }

        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }

        return historyManager;
    }
}
