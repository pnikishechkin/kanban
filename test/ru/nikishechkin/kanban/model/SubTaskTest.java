package ru.nikishechkin.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void equals_sameId() {
        // given
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание", 0);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание", 0);

        // when
        subTask1.id = 0;
        subTask2.id = 0;

        // then
        Assertions.assertEquals(subTask1, subTask2, "Эпики с одинаковыми идентификатороми не считаются одинаковыми");
    }

    @Test
    void setEpicId_sameSubTaskId() {

        // Попытка создать подзадачу, и назначить идентификатор ее эпика таким же, как у подзадачи
        // given
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание", 0);
        subTask1.setId(1);

        // when
        subTask1.setEpicId(1);

        // then
        Assertions.assertEquals(0, subTask1.getEpicId());
    }

}