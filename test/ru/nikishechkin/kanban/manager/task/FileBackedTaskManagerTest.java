package ru.nikishechkin.kanban.manager.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.manager.Managers;
import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.manager.history.InMemoryHistoryManager;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    /**
     * Создание менеджера задач и заполнение его дефолтными значениями из файла
     */
    @Override
    public void initData() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, "resources\\tasks.txt");
        // Заполнение задач дефолтными значениями из специального файла
        taskManager.loadFromFile("resources\\defaultTasks.txt");
    }

    /**
     * Создание менеджера задач заново и загрузка в него данных из привязанного файла
     */
    private void reloadFromFile() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, "resources\\tasks.txt");
        taskManager.loadFromFile("resources\\tasks.txt");
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

    @Test
    void changeTasksAndReloadFromFile() {
        // given
        initData();

        // when
        taskManager.deleteSubTask(9);
        taskManager.deleteSubTask(3);
        taskManager.deleteEpic(0);
        taskManager.deleteTask(11);
        reloadFromFile();

        // then
        Assertions.assertEquals(taskManager.getTasks().size(), 1);
        Assertions.assertEquals(taskManager.getEpics().size(), 2);
        Assertions.assertEquals(taskManager.getSubTasks().size(), 4);
    }
}
