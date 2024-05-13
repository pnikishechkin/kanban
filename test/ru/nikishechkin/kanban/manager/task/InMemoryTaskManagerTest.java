package ru.nikishechkin.kanban.manager.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.manager.history.InMemoryHistoryManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public void initData() {

        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);

        InMemoryTaskManager.idCounter = 0;

        taskManager.addEpic(new Epic("Эпик 1", "Описание")); // ID=0

        taskManager.addSubTask(new SubTask("Подзадача 1_1", "Описание",
                LocalDateTime.of(2024, 1, 1, 9, 30),
                Duration.ofMinutes(60), 0)); // ID=1
        taskManager.addSubTask(new SubTask("Подзадача 1_2", "Описание",
                LocalDateTime.of(2024, 2, 10, 9, 30),
                Duration.ofMinutes(10), 0)); // ID=2
        taskManager.addSubTask(new SubTask("Подзадача 1_3", "Описание",
                LocalDateTime.of(2024, 3, 15, 9, 30),
                Duration.ofMinutes(60), 0)); // ID=3

        taskManager.addEpic(new Epic("Эпик 2", "Описание")); // ID=4

        taskManager.addSubTask(new SubTask("Подзадача 2_1", "Описание",
                LocalDateTime.of(2024, 4, 1, 9, 30),
                Duration.ofMinutes(60), 4)); // ID=5
        taskManager.addSubTask(new SubTask("Подзадача 2_2", "Описание",
                LocalDateTime.of(2024, 5, 1, 9, 30),
                Duration.ofMinutes(60), 4)); // ID=6

        taskManager.addEpic(new Epic("Эпик 3", "Описание")); // ID=7

        taskManager.addSubTask(new SubTask("Подзадача 3_1", "Описание",
                LocalDateTime.of(2024, 6, 1, 9, 30),
                Duration.ofMinutes(60), 7)); // ID=8
        taskManager.addSubTask(new SubTask("Подзадача 3_2", "Описание",
                LocalDateTime.of(2024, 7, 1, 9, 30),
                Duration.ofMinutes(60), 7)); // ID=9
        taskManager.addSubTask(new SubTask("Подзадача 3_3", "Описание",
                LocalDateTime.of(2024, 8, 1, 9, 30),
                Duration.ofMinutes(60), 7)); // ID=10

        taskManager.addTask(new Task("Задача 1", "Описание",
                LocalDateTime.of(2024, 9, 1, 9, 30),
                Duration.ofMinutes(60))); // ID=11
        taskManager.addTask(new Task("Задача 2", "Описание",
                LocalDateTime.of(2024, 10, 10, 9, 00),
                Duration.ofMinutes(600))); // ID=12
    }

}