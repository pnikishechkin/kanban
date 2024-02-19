package ru.nikishechkin.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void equals_sameId() {
        // given
        Task task1 = new Task(0, "Задача 1", "Описание", TaskStatus.NEW);
        Task task2 = new Task(0, "Задача 2", "Описание", TaskStatus.NEW);

        // then
        Assertions.assertEquals(task1, task2, "Задачи с одинаковыми идентификатороми не считаются одинаковыми");
    }
}