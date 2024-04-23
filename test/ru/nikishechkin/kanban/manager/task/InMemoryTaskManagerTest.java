package ru.nikishechkin.kanban.manager.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.manager.history.InMemoryHistoryManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.util.HashSet;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
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
    @Override
    void getEpics_checkSize() {
        super.getEpics_checkSize();
    }

    @Test
    @Override
    void getSubTasks_checkSize() {
        super.getSubTasks_checkSize();
    }

    @Test
    @Override
    void getTasks_checkSize() {
        super.getTasks_checkSize();
    }

    @Test
    @Override
    void getEpicById_epicExist() {
        super.getEpicById_epicExist();
    }

    @Test
    @Override
    void getEpicById_epicNotExist() {
        super.getEpicById_epicNotExist();
    }

    @Test
    @Override
    void getSubTaskById_subTaskExist() {
        super.getSubTaskById_subTaskExist();
    }

    @Test
    @Override
    void getSubTaskById_subTaskNotExist() {
        super.getSubTaskById_subTaskNotExist();
    }

    @Test
    @Override
    void getTaskById_taskExist() {
        super.getTaskById_taskExist();
    }

    @Test
    @Override
    void getTaskById_taskNotExist() {
        super.getTaskById_taskNotExist();
    }

    @Test
    @Override
    void getSubTasksIds_checkList() {
        super.getSubTasksIds_checkList();
    }

    @Test
    @Override
    void editTask_changeStatus() {
        super.editTask_changeStatus();
    }

    @Test
    @Override
    void editSubTask_changeStatusSubTaskAndEpic() {
        super.editSubTask_changeStatusSubTaskAndEpic();
    }

    @Test
    @Override
    void editSubTask_changeStatusSubTask() {
        super.editSubTask_changeStatusSubTask();
    }

    @Test
    @Override
    void clearTasks_taskCountIsZero() {
        super.clearTasks_taskCountIsZero();
    }

    @Test
    @Override
    void clearSubTasks_subTaskCountIsZero() {
        super.clearSubTasks_subTaskCountIsZero();
    }

    @Test
    @Override
    void clearEpics_epicsCountAndSubTasksIsZero() {
        super.clearEpics_epicsCountAndSubTasksIsZero();
    }

    @Test
    @Override
    void getHistory_historyUpdate() {
        super.getHistory_historyUpdate();
    }

    @Test
    @Override
    void removeSubTask_epicUpdateId() {
        super.removeSubTask_epicUpdateId();
    }

    @Test
    @Override
    void removeTasks_historyUpdate() {
        super.removeTasks_historyUpdate();
    }

}