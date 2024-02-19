package ru.nikishechkin.kanban.manager.history;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.model.Task;

class InMemoryHistoryManagerTest {

    @Test
    public void addTasks_countShouldBe10() {
        // given
        // Добавляем задачи в список, больше чем предельное количество
        HistoryManager historyManager = new InMemoryHistoryManager();
        for (int i = 0; i < InMemoryHistoryManager.COUNT_HISTORY_TASKS + 5; i++) {
            historyManager.add(new Task("Задача", "Описание"));
        }

        // when
        int countTasksInHistory = historyManager.getHistory().size();

        // then
        // Количество задач не должно превышать максимального значения
        Assertions.assertEquals(InMemoryHistoryManager.COUNT_HISTORY_TASKS, countTasksInHistory);
    }

}