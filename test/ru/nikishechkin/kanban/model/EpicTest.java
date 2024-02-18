package ru.nikishechkin.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testEpicsEqualsById() {
        Epic epic1 = new Epic("Эпик 1", "Описание");
        epic1.id = 0;
        Epic epic2 = new Epic("Эпик 2", "Описание");
        epic2.id = 0;
        Assertions.assertEquals(epic1, epic2, "Эпики с одинаковыми идентификатороми не считаются одинаковыми");
    }

    @Test
    void testAddSubTaskWithEpicId() {
        Epic epic1 = new Epic("Эпик 1", "Описание");
        epic1.addSubTask(epic1.getId());

        Assertions.assertFalse(epic1.getSubTasksIds().contains(0),
                "Эпику можно назначить подзадачу с таким же идентификатором, как у самого эпика");
    }

}