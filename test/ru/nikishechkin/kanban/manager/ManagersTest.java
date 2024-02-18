package ru.nikishechkin.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        Assertions.assertNotNull(Managers.getDefault(), "Менеджер задач по умолчанию не создается");
    }

    @Test
    void getDefaultHistory() {
        Assertions.assertNotNull(Managers.getDefaultHistory(),
                "Менеджер истории просмотра задач по умолчанию не создается");
    }
}