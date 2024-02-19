package ru.nikishechkin.kanban.manager.history;

import ru.nikishechkin.kanban.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    protected final static int COUNT_HISTORY_TASKS = 10;
    private ArrayList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    /**
     * Добавить задачу в список историй
     * @param task задача, подзадача или эпик
     */
    @Override
    public void add(Task task) {

        this.history.add(task);

        // Проверить размер списка с историей просмотров задач и скорректировать при необходимости
        if (history.size() >= COUNT_HISTORY_TASKS) {
            for (int i = 0; i < history.size() - COUNT_HISTORY_TASKS; i++) {
                history.remove(0);
            }
        }
    }

    /**
     * Получить историю просмотров (последние 10 задач)
     * @return список с последними просмотренными задачами
     */
    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
