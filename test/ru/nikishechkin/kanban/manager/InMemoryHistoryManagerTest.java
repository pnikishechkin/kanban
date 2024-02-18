package ru.nikishechkin.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    static TaskManager taskManager;
    static HistoryManager historyManager;

    @BeforeEach
    public void initData() {
        InMemoryTaskManager.idCounter = 0;
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        taskManager.addEpic(new Epic("Эпик 1", "Описание")); // ID=0

        taskManager.addSubTask(new SubTask("Подзадача 1_1", "Описание", 0)); // ID=1
        taskManager.addSubTask(new SubTask("Подзадача 1_2", "Описание", 0)); // ID=2
        taskManager.addSubTask(new SubTask("Подзадача 1_3", "Описание", 0)); // ID=3

        taskManager.addEpic(new Epic("Эпик 2", "Описание")); // ID=4
        taskManager.addSubTask(new SubTask("Подзадача 2_1", "Описание", 4)); // ID=5
        taskManager.addSubTask(new SubTask("Подзадача 2_2", "Описание", 4)); // ID=6
    }

    @Test
    public void countShouldBeLess10() {

        HistoryManager historyManager = new InMemoryHistoryManager();

        Assertions.assertTrue(historyManager.getHistory().size() <= InMemoryHistoryManager.COUNT_HISTORY_TASKS);

        // Добавляем задачи в список, больше чем предельное количество
        for (int i = 0; i < InMemoryHistoryManager.COUNT_HISTORY_TASKS + 5; i++) {
            historyManager.add(new Task("Задача", "Описание"));
        }

        // Количество задач не должно превышать максимального значения
        Assertions.assertTrue(historyManager.getHistory().size() <= InMemoryHistoryManager.COUNT_HISTORY_TASKS);
    }

    @Test
    public void correctHistoryTasks() {

        taskManager.getEpicById(0);
        taskManager.getEpicById(4);
        taskManager.getEpicById(0);
        taskManager.getSubTaskById(1);
        taskManager.getSubTaskById(3);
        taskManager.getSubTaskById(2);
        taskManager.getSubTaskById(6);
        taskManager.getSubTaskById(5);
        taskManager.getEpicById(4);
        taskManager.getEpicById(0);
        taskManager.getSubTaskById(3);

        Assertions.assertEquals(4, historyManager.getHistory().get(0).getId());
        Assertions.assertEquals(0, historyManager.getHistory().get(1).getId());
        Assertions.assertEquals(3, historyManager.getHistory().get(9).getId());

        Assertions.assertEquals(InMemoryHistoryManager.COUNT_HISTORY_TASKS, historyManager.getHistory().size());
    }
}