package ru.nikishechkin.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void equals_sameId() {
        //given
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Epic epic2 = new Epic("Эпик 2", "Описание");

        // when
        epic1.id = 0;
        epic2.id = 0;

        // then
        Assertions.assertEquals(epic1, epic2, "Эпики с одинаковыми идентификатороми не считаются одинаковыми");
    }

    @Test
    void addSubTask_subtaskNotAdded_subtaskIdEqualEpicId() {
        // given
        Epic epic1 = new Epic("Эпик 1", "Описание");

        // when
        epic1.addSubTask(epic1.getId());
        boolean containsSubTask = epic1.getSubTasksIds().contains(0);

        // then
        Assertions.assertFalse(containsSubTask,
                "Эпику можно назначить подзадачу с таким же идентификатором, как у самого эпика");
    }

}