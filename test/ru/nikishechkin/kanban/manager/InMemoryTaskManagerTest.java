package ru.nikishechkin.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    static TaskManager taskManager;

    @BeforeEach
    public void initData() {

        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);

        InMemoryTaskManager.idCounter = 0;

        taskManager.addEpic(new Epic("Эпик 1", "Описание")); // ID=0

        taskManager.addSubTask(new SubTask("Подзадача 1_1", "Описание", 0)); // ID=1
        taskManager.addSubTask(new SubTask("Подзадача 1_2", "Описание", 0)); // ID=2
        taskManager.addSubTask(new SubTask("Подзадача 1_3", "Описание", 0)); // ID=3

        taskManager.addEpic(new Epic("Эпик 2", "Описание")); // ID=4
        taskManager.addSubTask(new SubTask("Подзадача 2_1", "Описание", 4)); // ID=5
        taskManager.addSubTask(new SubTask("Подзадача 2_2", "Описание", 4)); // ID=6

        taskManager.addEpic(new Epic("Эпик 3", "Описание")); // ID=7
        taskManager.addSubTask(new SubTask("Подзадача 3_1", "Описание", 7)); // ID=8
        taskManager.addSubTask(new SubTask("Подзадача 3_2", "Описание", 7)); // ID=9
        taskManager.addSubTask(new SubTask("Подзадача 3_3", "Описание", 7)); // ID=10

        taskManager.addTask(new Task("Задача 1", "Описание")); // ID=11
        taskManager.addTask(new Task("Задача 2", "Описание")); // ID=12
    }

    @Test
    void checkCountTasks() {
        Assertions.assertEquals(3, taskManager.getEpics().size());
        Assertions.assertEquals(8, taskManager.getSubTasks().size());
        Assertions.assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    void checkGetEpics() {
        Assertions.assertNotNull(taskManager.getEpicById(0), "Ошибка поиска эпика по идентификатору");
        Assertions.assertNotNull(taskManager.getEpicById(4), "Ошибка поиска эпика по идентификатору");
        Assertions.assertNotNull(taskManager.getEpicById(7), "Ошибка поиска эпика по идентификатору");

        // В случае поиска эпика по идентификатору других типов задач, должен вернуть null
        Assertions.assertNull(taskManager.getEpicById(1), "Ошибка поиска эпика по идентификатору");
        Assertions.assertNull(taskManager.getEpicById(11), "Ошибка поиска эпика по идентификатору");
    }

    @Test
    void checkGetSubTasks() {
        Assertions.assertNotNull(taskManager.getSubTaskById(1), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(2), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(3), "Ошибка поиска подзадачи по идентификатору");

        Assertions.assertNotNull(taskManager.getSubTaskById(5), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(6), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(8), "Ошибка поиска подзадачи по идентификатору");

        Assertions.assertNotNull(taskManager.getSubTaskById(9), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(10), "Ошибка поиска подзадачи по идентификатору");

        // В случае поиска подзадачи по идентификатору других типов задач, должен вернуть null
        Assertions.assertNull(taskManager.getSubTaskById(0), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNull(taskManager.getSubTaskById(11), "Ошибка поиска подзадачи по идентификатору");
    }

    @Test
    void checkGetTasks() {
        Assertions.assertNotNull(taskManager.getTaskById(11), "Ошибка поиска задачи по идентификатору");
        Assertions.assertNotNull(taskManager.getTaskById(12), "Ошибка поиска задачи по идентификатору");

        // В случае поиска задачи по идентификатору других типов задач, должен вернуть null
        Assertions.assertNull(taskManager.getTaskById(0), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNull(taskManager.getTaskById(5), "Ошибка поиска подзадачи по идентификатору");
    }

    @Test
    void checkEpicSubTasksHashSet() {

        HashSet<Integer> subTasks = new HashSet<>();
        subTasks.add(1);
        subTasks.add(2);
        subTasks.add(3);

        Assertions.assertEquals(subTasks, taskManager.getEpicById(0).getSubTasksIds());
    }

    @Test
    void checkChangeTasksStatus() {
        taskManager.getTaskById(11).setStatus(TaskStatus.IN_PROGRESS);
        taskManager.getTaskById(12).setStatus(TaskStatus.DONE);

        taskManager.editTask(taskManager.getTaskById(11));
        taskManager.editTask(taskManager.getTaskById(12));

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(11).getStatus());
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getTaskById(12).getStatus());
    }

    @Test
    void checkChangeSubTasksAndEpicStatus() {
        taskManager.getSubTaskById(5).setStatus(TaskStatus.IN_PROGRESS);
        taskManager.getSubTaskById(6).setStatus(TaskStatus.IN_PROGRESS);

        taskManager.editSubTask(taskManager.getSubTaskById(5));
        taskManager.editSubTask(taskManager.getSubTaskById(6));

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubTaskById(5).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubTaskById(6).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(4).getStatus());
    }

    @Test
    void checkChangeSubTasksAndEpicStatus2() {
        taskManager.getSubTaskById(5).setStatus(TaskStatus.DONE);
        taskManager.getSubTaskById(6).setStatus(TaskStatus.IN_PROGRESS);

        taskManager.editSubTask(taskManager.getSubTaskById(5));
        taskManager.editSubTask(taskManager.getSubTaskById(6));

        Assertions.assertEquals(TaskStatus.DONE, taskManager.getSubTaskById(5).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubTaskById(6).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(4).getStatus());
    }

    @Test
    void checkClearTasks() {
        taskManager.clearTasks();
        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void checkClearSubTasks() {
        taskManager.clearSubTasks();
        Assertions.assertEquals(0, taskManager.getSubTasks().size());
        // Проверка, что у всех эпиков нет связанных подзадач
        Assertions.assertEquals(0, taskManager.getEpicById(0).getSubTasksIds().size());
        Assertions.assertEquals(0, taskManager.getEpicById(4).getSubTasksIds().size());
        Assertions.assertEquals(0, taskManager.getEpicById(7).getSubTasksIds().size());
    }

    @Test
    void checkClearEpics() {
        taskManager.clearEpics();
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubTasks().size());
    }

}