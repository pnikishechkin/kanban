package ru.nikishechkin.kanban.manager.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.manager.history.InMemoryHistoryManager;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    /**
     * Создание менеджера задач и заполнение его дефолтными значениями из файла
     */
    @Override
    public void initData() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, "resources\\tasks.csv");
        // Заполнение задач дефолтными значениями из специального файла
        taskManager.loadFromFile("resources\\defaultTasks.csv");
    }

    /**
     * Создание менеджера задач заново и загрузка в него данных из привязанного файла
     */
    private void reloadFromFile() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, "resources\\tasks.csv");
        taskManager.loadFromFile("resources\\tasks.csv");
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
        // given
        initData();

        // when
        // Просмотрели эпики, подзадачи
        taskManager.getEpicById(0);
        taskManager.getEpicById(4);
        taskManager.getEpicById(0);
        taskManager.getSubTaskById(1);
        taskManager.getSubTaskById(3);
        taskManager.getSubTaskById(2);
        taskManager.getSubTaskById(6);
        taskManager.getSubTaskById(5);
        taskManager.getEpicById(4);
        taskManager.getEpicById(0);
        taskManager.getSubTaskById(3);

        // Количество уникальных задач в истории - 9
        Assertions.assertEquals(9, taskManager.getHistory().size());
        Assertions.assertEquals(3, taskManager.getHistory().get(8).getId());
        Assertions.assertEquals(0, taskManager.getHistory().get(7).getId());
        Assertions.assertEquals(9, taskManager.getHistory().get(1).getId());
        Assertions.assertEquals(7, taskManager.getHistory().get(0).getId());

        taskManager.getSubTaskById(2);
        taskManager.getSubTaskById(6);

        Assertions.assertEquals(9, taskManager.getHistory().size());
        Assertions.assertEquals(6, taskManager.getHistory().get(8).getId());
        Assertions.assertEquals(2, taskManager.getHistory().get(7).getId());
    }

    @Test
    @Override
    void removeSubTask_epicUpdateId() {
        super.removeSubTask_epicUpdateId();
    }

    @Test
    void historyInitFromFile() {
        // given
        initData();

        // then
        // 0,4,7,9,6,3
        Assertions.assertEquals(6, taskManager.getHistory().size());
        Assertions.assertEquals(0, taskManager.getHistory().get(0).getId());
        Assertions.assertEquals(4, taskManager.getHistory().get(1).getId());
        Assertions.assertEquals(7, taskManager.getHistory().get(2).getId());
        Assertions.assertEquals(9, taskManager.getHistory().get(3).getId());
        Assertions.assertEquals(6, taskManager.getHistory().get(4).getId());
        Assertions.assertEquals(3, taskManager.getHistory().get(5).getId());
    }

    @Test
    @Override
    void removeTasks_historyUpdate() {

        // given
        initData();

        // when
        // Просмотрели эпики, подзадачи
        taskManager.getEpicById(0);
        taskManager.getEpicById(4);
        taskManager.getEpicById(0);
        taskManager.getSubTaskById(1);
        taskManager.getSubTaskById(3);
        taskManager.getSubTaskById(2);
        taskManager.getSubTaskById(6);
        taskManager.getSubTaskById(5);
        taskManager.getEpicById(4);
        taskManager.getEpicById(0);
        taskManager.getSubTaskById(3);

        // Количество уникальных задач в истории - 9
        Assertions.assertEquals(9, taskManager.getHistory().size());
        Assertions.assertEquals(3, taskManager.getHistory().get(8).getId());
        Assertions.assertEquals(0, taskManager.getHistory().get(7).getId());
        Assertions.assertEquals(9, taskManager.getHistory().get(1).getId());
        Assertions.assertEquals(7, taskManager.getHistory().get(0).getId());

        // Удаляем эпик с ID 0, привязанные задачи: ID 1, 2, 3
        taskManager.deleteEpic(0);

        Assertions.assertEquals(5, taskManager.getHistory().size());
        Assertions.assertEquals(4, taskManager.getHistory().get(4).getId());
        Assertions.assertEquals(5, taskManager.getHistory().get(3).getId());
        Assertions.assertEquals(6, taskManager.getHistory().get(2).getId());
        Assertions.assertEquals(9, taskManager.getHistory().get(1).getId());
        Assertions.assertEquals(7, taskManager.getHistory().get(0).getId());

        // Просмотрели задачу с идентификатором 11
        taskManager.getTaskById(11);

        Assertions.assertEquals(6, taskManager.getHistory().size());
        Assertions.assertEquals(11, taskManager.getHistory().get(5).getId());

        // Удалили задачу с идентификатором 11
        taskManager.deleteTask(11);

        Assertions.assertEquals(5, taskManager.getHistory().size());
        Assertions.assertEquals(4, taskManager.getHistory().get(4).getId());
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
