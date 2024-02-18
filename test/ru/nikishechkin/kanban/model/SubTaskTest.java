package ru.nikishechkin.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void testSubTasksEqualsById() {
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание", 0);
        subTask1.id = 0;
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание", 0);
        subTask2.id = 0;
        Assertions.assertEquals(subTask1, subTask2, "Эпики с одинаковыми идентификатороми не считаются одинаковыми");
    }

    @Test
    void testSetSubTaskEpicIdEqualsSubTaskId() {

        // Попытка создать подзадачу, и назначить идентификатор ее эпика таким же, как у подзадачи
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание", 0);

        subTask1.setId(1);
        subTask1.setEpicId(1);

        Assertions.assertEquals(0, subTask1.getEpicId());
    }

}