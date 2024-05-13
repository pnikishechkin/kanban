package ru.nikishechkin.kanban.manager.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.manager.Managers;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    /**
     * Создание менеджера задач и заполнение его дефолтными значениями из файла
     */
    @Override
    public void initData() {

        // Заполнение файла с задачами дефолтными тестовыми данными
        try {
            Files.copy(Path.of("resources\\defaultTasks.csv"), Path.of("resources\\tasks.csv"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Загрузка задач из файла
        taskManager = Managers.getFileBackedTaskManager("resources\\tasks.csv");
    }

    public void initEmptyData() {
        // Очистка файла с задачами
        try {
            Path path = Paths.get("resources\\tasks.csv");
            Files.writeString(path, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Загрузка задач из файла
        taskManager = Managers.getFileBackedTaskManager("resources\\tasks.csv");
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

        // Проверка привязки сабтасков к эпикам
        Assertions.assertEquals(3, taskManager.getEpicById(0).getSubTasksIds().size());
        Assertions.assertTrue(taskManager.getEpicById(0).getSubTasksIds().contains(1));
        Assertions.assertTrue(taskManager.getEpicById(0).getSubTasksIds().contains(2));
        Assertions.assertTrue(taskManager.getEpicById(0).getSubTasksIds().contains(3));

        Assertions.assertEquals(2, taskManager.getEpicById(4).getSubTasksIds().size());
        Assertions.assertEquals(3, taskManager.getEpicById(7).getSubTasksIds().size());
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
        // Загрузка заново из файла
        taskManager = Managers.getFileBackedTaskManager("resources\\tasks.csv");

        // then
        Assertions.assertEquals(taskManager.getTasks().size(), 1);
        Assertions.assertEquals(taskManager.getEpics().size(), 2);
        Assertions.assertEquals(taskManager.getSubTasks().size(), 4);
    }

    @Test
    void createTwoFileManagers() {
        // given
        initEmptyData();

        taskManager.addEpic(new Epic("Эпик 3", "Описание"));
        taskManager.addSubTask(new SubTask("Подзадача 3_1", "Описание",
                LocalDateTime.of(2023, 5, 1, 9, 30),
                Duration.ofMinutes(60),
                taskManager.getEpics().get(0).getId()));
        taskManager.addSubTask(new SubTask("Подзадача 3_2", "Описание", LocalDateTime.of(2023, 5, 1, 9, 30),
                Duration.ofMinutes(60),
                taskManager.getEpics().get(0).getId()));
        taskManager.addSubTask(new SubTask("Подзадача 3_3", "Описание", LocalDateTime.of(2023, 5, 1, 9, 30),
                Duration.ofMinutes(60),
                taskManager.getEpics().get(0).getId()));

        taskManager.addTask(new Task("Задача 1", "Описание",
                LocalDateTime.of(2023, 5, 1, 9, 30),
                Duration.ofMinutes(60)));
        taskManager.addTask(new Task("Задача 2", "Описание",
                LocalDateTime.of(2023, 5, 1, 9, 30),
                Duration.ofMinutes(60)));

        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(3, taskManager.getSubTasks().size());
        Assertions.assertEquals(2, taskManager.getTasks().size());

        FileBackedTaskManager taskManager2 = Managers.getFileBackedTaskManager("resources\\tasks.csv");
        Assertions.assertEquals(1, taskManager2.getEpics().size());
        Assertions.assertEquals(3, taskManager2.getSubTasks().size());
        Assertions.assertEquals(2, taskManager2.getTasks().size());

        taskManager2.addTask(new Task("Задача 3", "Описание",
                LocalDateTime.of(2023, 5, 1, 9, 30),
                Duration.ofMinutes(60)));

        Assertions.assertEquals(3, taskManager2.getTasks().size());
    }

    @Test
    void openFileNotExist_createException() {
        // Загрузка задач из файла
        Assertions.assertThrows(RuntimeException.class, () -> {
            Managers.getFileBackedTaskManager("resources\\errorName.csv");
        });
    }
}
