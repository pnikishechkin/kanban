package ru.nikishechkin.kanban.manager.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.manager.history.InMemoryHistoryManager;
import ru.nikishechkin.kanban.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class TaskManagerTest<T extends InMemoryTaskManager> {

    protected T taskManager;

    public abstract void initData();

    void getEpics_checkSize() {
        // given
        initData();

        // then
        Assertions.assertEquals(3, taskManager.getEpics().size());
    }

    @Test
    void getSubTasks_checkSize() {
        // given
        initData();

        // then
        Assertions.assertEquals(8, taskManager.getSubTasks().size());
    }

    @Test
    void getTasks_checkSize() {
        // given
        initData();

        // then
        Assertions.assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    void getEpicById_epicExist() {
        // given
        initData();

        // then
        Assertions.assertNotNull(taskManager.getEpicById(0), "Ошибка поиска эпика по идентификатору");
        Assertions.assertNotNull(taskManager.getEpicById(4), "Ошибка поиска эпика по идентификатору");
        Assertions.assertNotNull(taskManager.getEpicById(7), "Ошибка поиска эпика по идентификатору");

        // В случае поиска эпика по идентификатору других типов задач, должен вернуть null
        Assertions.assertNull(taskManager.getEpicById(1), "Ошибка поиска эпика по идентификатору");
        Assertions.assertNull(taskManager.getEpicById(11), "Ошибка поиска эпика по идентификатору");
    }

    @Test
    void getEpicById_epicNotExist() {
        // given
        initData();

        // then
        Assertions.assertNull(taskManager.getEpicById(1), "Ошибка поиска эпика по идентификатору");
        Assertions.assertNull(taskManager.getEpicById(11), "Ошибка поиска эпика по идентификатору");
    }

    @Test
    void getSubTaskById_subTaskExist() {
        // given
        initData();

        // then
        Assertions.assertNotNull(taskManager.getSubTaskById(1), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(2), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(3), "Ошибка поиска подзадачи по идентификатору");

        Assertions.assertNotNull(taskManager.getSubTaskById(5), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(6), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(8), "Ошибка поиска подзадачи по идентификатору");

        Assertions.assertNotNull(taskManager.getSubTaskById(9), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNotNull(taskManager.getSubTaskById(10), "Ошибка поиска подзадачи по идентификатору");
    }

    @Test
    void getSubTaskById_subTaskNotExist() {
        // given
        initData();

        // then
        // В случае поиска подзадачи по идентификатору других типов задач, должен вернуть null
        Assertions.assertNull(taskManager.getSubTaskById(0), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNull(taskManager.getSubTaskById(11), "Ошибка поиска подзадачи по идентификатору");
    }

    @Test
    void getTaskById_taskExist() {
        // given
        initData();

        // then
        Assertions.assertNotNull(taskManager.getTaskById(11), "Ошибка поиска задачи по идентификатору");
        Assertions.assertNotNull(taskManager.getTaskById(12), "Ошибка поиска задачи по идентификатору");
    }

    @Test
    void getTaskById_taskNotExist() {
        // given
        initData();

        // then
        // В случае поиска задачи по идентификатору других типов задач, должен вернуть null
        Assertions.assertNull(taskManager.getTaskById(0), "Ошибка поиска подзадачи по идентификатору");
        Assertions.assertNull(taskManager.getTaskById(5), "Ошибка поиска подзадачи по идентификатору");
    }

    @Test
    void getSubTasksIds_checkList() {
        // given
        initData();
        HashSet<Integer> subTasks = new HashSet<>();
        subTasks.add(1);
        subTasks.add(2);
        subTasks.add(3);

        // then
        Assertions.assertEquals(subTasks, taskManager.getEpicById(0).getSubTasksIds());
    }

    @Test
    void editTask_changeStatus() {
        // given
        initData();

        // when
        taskManager.getTaskById(11).setStatus(TaskStatus.IN_PROGRESS);
        taskManager.getTaskById(12).setStatus(TaskStatus.DONE);
        taskManager.editTask(taskManager.getTaskById(11));
        taskManager.editTask(taskManager.getTaskById(12));

        // then
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(11).getStatus());
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getTaskById(12).getStatus());
    }

    @Test
    void editSubTask_changeStatusSubTaskAndEpic() {
        // given
        initData();

        // when
        taskManager.getSubTaskById(5).setStatus(TaskStatus.IN_PROGRESS);
        taskManager.getSubTaskById(6).setStatus(TaskStatus.IN_PROGRESS);
        taskManager.editSubTask(taskManager.getSubTaskById(5));
        taskManager.editSubTask(taskManager.getSubTaskById(6));

        // then
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubTaskById(5).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubTaskById(6).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(4).getStatus());
    }

    @Test
    void editSubTask_changeStatusSubTask() {
        // given
        initData();

        // when
        taskManager.getSubTaskById(5).setStatus(TaskStatus.DONE);
        taskManager.getSubTaskById(6).setStatus(TaskStatus.IN_PROGRESS);
        taskManager.editSubTask(taskManager.getSubTaskById(5));
        taskManager.editSubTask(taskManager.getSubTaskById(6));

        // then
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getSubTaskById(5).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubTaskById(6).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(4).getStatus());
    }

    @Test
    void clearTasks_taskCountIsZero() {
        // given
        initData();

        // when
        taskManager.clearTasks();

        // then
        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void clearSubTasks_subTaskCountIsZero() {
        // given
        initData();

        // when
        taskManager.clearSubTasks();

        // then
        Assertions.assertEquals(0, taskManager.getSubTasks().size());
        // Проверка, что у всех эпиков нет связанных подзадач
        Assertions.assertEquals(0, taskManager.getEpicById(0).getSubTasksIds().size());
        Assertions.assertEquals(0, taskManager.getEpicById(4).getSubTasksIds().size());
        Assertions.assertEquals(0, taskManager.getEpicById(7).getSubTasksIds().size());
    }

    @Test
    void clearEpics_epicsCountAndSubTasksIsZero() {
        // given
        initData();

        // when
        taskManager.clearEpics();

        // then
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    void getHistory_historyUpdate() {
        // given
        initData();

        // when
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

        // then
        Assertions.assertEquals(7, taskManager.getHistory().size());
        Assertions.assertEquals(1, taskManager.getHistory().get(0).getId());
        Assertions.assertEquals(2, taskManager.getHistory().get(1).getId());
        Assertions.assertEquals(3, taskManager.getHistory().get(6).getId());

        // when
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(6);

        // then
        Assertions.assertEquals(7, taskManager.getHistory().size());
        Assertions.assertEquals(5, taskManager.getHistory().get(5).getId());
        Assertions.assertEquals(6, taskManager.getHistory().get(6).getId());
    }

    @Test
    void removeSubTask_epicUpdateId() {
        // given
        initData();

        // when
        taskManager.deleteSubTask(1);
        taskManager.deleteSubTask(3);

        // then
        Assertions.assertEquals(1, taskManager.getEpicById(0).getSubTasksIds().size());
        Assertions.assertTrue(taskManager.getEpicById(0).getSubTasksIds().contains(2));
        Assertions.assertFalse(taskManager.getEpicById(0).getSubTasksIds().contains(1));
        Assertions.assertFalse(taskManager.getEpicById(0).getSubTasksIds().contains(3));
    }

    @Test
    void removeTasks_historyUpdate() {
        // given
        initData();

        // История просмотра пустая
        Assertions.assertEquals(0, taskManager.getHistory().size());

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

        // Количество уникальных задач - 7
        Assertions.assertEquals(7, taskManager.getHistory().size());
        Assertions.assertEquals(0, taskManager.getHistory().get(5).getId());
        Assertions.assertEquals(3, taskManager.getHistory().get(6).getId());

        // Удаляем эпик с ID 0, привязанные задачи: ID 1, 2, 3
        taskManager.deleteEpic(0);

        Assertions.assertEquals(3, taskManager.getHistory().size());
        Assertions.assertEquals(4, taskManager.getHistory().get(2).getId());

        // Просмотрели задачу с идентификатором 11
        taskManager.getTaskById(11);

        Assertions.assertEquals(4, taskManager.getHistory().size());
        Assertions.assertEquals(11, taskManager.getHistory().get(3).getId());

        // Удалили задачу с идентификатором 11
        taskManager.deleteTask(11);

        Assertions.assertEquals(3, taskManager.getHistory().size());
        Assertions.assertEquals(4, taskManager.getHistory().get(2).getId());

        // Очистили все
        taskManager.clearTasks();
        taskManager.clearSubTasks();;
        taskManager.clearEpics();

        Assertions.assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    void initTasks_checkDates() {
        // given
        initData();

        // when
        Task task1 = taskManager.getTaskById(11);
        Task task2 = taskManager.getTaskById(12);

        // then
        Assertions.assertEquals(LocalDateTime.of(2024, 9, 1, 9, 30),
                task1.getStartTime().get());
        Assertions.assertEquals(60,
                task1.getDuration().toMinutes());
        Assertions.assertEquals(LocalDateTime.of(2024, 9, 1, 10, 30),
                task1.getEndTime());

        Assertions.assertEquals(LocalDateTime.of(2024, 10, 10, 9, 0),
                task2.getStartTime().get());
        Assertions.assertEquals(600,
                task2.getDuration().toMinutes());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 10, 19, 0),
                task2.getEndTime());
    }

    @Test
    void changeTasks_checkDates() {
        // given
        initData();

        // when
        Task task1 = taskManager.getTaskById(11);
        task1.setStartTime(LocalDateTime.of(2024, 6, 12, 15, 30));
        task1.setDuration(Duration.ofMinutes(30));

        // then
        Assertions.assertEquals(LocalDateTime.of(2024, 6, 12, 16, 00),
                task1.getEndTime());
    }

    @Test
    void initEpics_checkDates() {
        // given
        initData();

        // when
        Epic epic = taskManager.getEpicById(0);

        // then
        Assertions.assertEquals(LocalDateTime.of(2024, 1, 1, 9, 30),
                epic.getStartTime().get());
        Assertions.assertEquals(LocalDateTime.of(2024, 3, 15, 10, 30),
                epic.getEndTime());
        Assertions.assertEquals(Duration.ofMinutes(130), epic.getDuration());
    }

    @Test
    void changeEpics_checkDates() {
        // given
        initData();

        // when
        Epic epic = taskManager.getEpicById(0);

        SubTask subTask1 = taskManager.getSubTaskById(1);
        SubTask updatedSubTask1 = new SubTask(subTask1.getId(),
                subTask1.getName(), subTask1.getDescription(), subTask1.getStatus(),
                LocalDateTime.of(2024, 1, 1, 19, 30), Duration.ofMinutes(50),
                subTask1.getEpicId());
        taskManager.editSubTask(updatedSubTask1);

        SubTask subTask2 = taskManager.getSubTaskById(2);
        SubTask updatedSubTask2 = new SubTask(subTask2.getId(),
                subTask2.getName(), subTask2.getDescription(), subTask2.getStatus(),
                LocalDateTime.of(2024, 10, 1, 8, 30), Duration.ofMinutes(30),
                subTask2.getEpicId());
        taskManager.editSubTask(updatedSubTask2);

        // then
        Assertions.assertEquals(LocalDateTime.of(2024, 1, 1, 19, 30),
                epic.getStartTime().get());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 1, 9, 0),
                epic.getEndTime());

        // when
        taskManager.deleteSubTask(2);

        // then
        Assertions.assertEquals(LocalDateTime.of(2024, 1, 1, 19, 30),
                epic.getStartTime().get());
        Assertions.assertEquals(LocalDateTime.of(2024, 3, 15, 10, 30),
                epic.getEndTime());
        Assertions.assertEquals(Duration.ofMinutes(110), epic.getDuration());

    }

    @Test
    void initAndChangeTasks_checkPrioritizedTasksList() {

        // given
        initData();

        // then
        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        Assertions.assertEquals(10, prioritizedTasks.size());
        List<Task> listPrioritizedTasks = prioritizedTasks.stream().collect(Collectors.toList());
        Assertions.assertEquals(LocalDateTime.of(2024, 1, 1, 9, 30),
                listPrioritizedTasks.get(0).getStartTime().get());

        // when
        SubTask subTask1 = taskManager.getSubTaskById(1);
        SubTask updatedSubTask1 = new SubTask(subTask1.getId(),
                subTask1.getName(), subTask1.getDescription(), subTask1.getStatus(),
                LocalDateTime.of(2024, 5, 1, 19, 30), Duration.ofMinutes(50),
                subTask1.getEpicId());
        taskManager.editSubTask(updatedSubTask1);

        // then
        Assertions.assertEquals(10, prioritizedTasks.size());
        listPrioritizedTasks = prioritizedTasks.stream().collect(Collectors.toList());
        Assertions.assertEquals(LocalDateTime.of(2024, 2, 10, 9, 30),
                listPrioritizedTasks.get(0).getStartTime().get());

        // when
        taskManager.deleteSubTask(2);
        taskManager.deleteTask(11);

        // then
        Assertions.assertEquals(8, prioritizedTasks.size());
        listPrioritizedTasks = prioritizedTasks.stream().collect(Collectors.toList());
        Assertions.assertEquals(LocalDateTime.of(2024, 3, 15, 9, 30),
                listPrioritizedTasks.get(0).getStartTime().get());

        // when
        taskManager.deleteEpic(0);

        // then
        Assertions.assertEquals(6, prioritizedTasks.size());
        listPrioritizedTasks = prioritizedTasks.stream().collect(Collectors.toList());
        Assertions.assertEquals(LocalDateTime.of(2024, 4, 1, 9, 30),
                listPrioritizedTasks.get(0).getStartTime().get());
    }

    @Test
    void addTaskWithOverlapDates_checkAdded() {

        // given
        initData();

        // then
        Assertions.assertEquals(10, taskManager.getPrioritizedTasks().size());
        Assertions.assertEquals(2, taskManager.getTasks().size());
        Assertions.assertEquals(8, taskManager.getSubTasks().size());

        // when
        taskManager.addTask(new Task("Задача новая", "Пересекается по датам с имеющейся",
                LocalDateTime.of(2024, 10, 10, 12, 00),
                Duration.ofMinutes(600)));

        taskManager.addSubTask(new SubTask("Подзадача новая", "Пересекается по датам с уже имеющейся",
                LocalDateTime.of(2024, 8, 1, 9, 20),
                Duration.ofMinutes(60), 7));

        taskManager.addSubTask(new SubTask("Подзадача новая", "Не пересекается по датам с имеющейся",
                LocalDateTime.of(2025, 8, 1, 9, 30),
                Duration.ofMinutes(60), 7));

        // then
        Assertions.assertEquals(11, taskManager.getPrioritizedTasks().size());
        Assertions.assertEquals(2, taskManager.getTasks().size());
        Assertions.assertEquals(9, taskManager.getSubTasks().size());
    }

    @Test
    void addTask_addTaskToPrioritizedList_taskContainsStartTime() {
        // given
        initData();

        // when
        Task newTask = new Task("Задача новая", "Имеет дату, должна попасть в prioritizedList",
                LocalDateTime.of(2025, 10, 10, 12, 00),
                Duration.ofMinutes(600));
        taskManager.addTask(newTask);

        // then
        Assertions.assertEquals(11, taskManager.prioritizedTasks.size());
        Assertions.assertTrue(taskManager.prioritizedTasks.contains(newTask));
    }

    @Test
    void addTask_notAddTaskToPrioritizedList_taskNotContainsStartTime() {
        // given
        initData();

        // when
        Task newTask = new Task("Задача новая", "Не имеет даты, не должна попасть в prioritizedList",
                null,
                Duration.ofMinutes(600));
        taskManager.addTask(newTask);

        // then
        Assertions.assertEquals(10, taskManager.prioritizedTasks.size());
        Assertions.assertFalse(taskManager.prioritizedTasks.contains(newTask));
    }

    @Test
    void addSubTask_addSubTaskToPrioritizedList_subTaskContainsStartTime() {
        // given
        initData();

        // when
        SubTask newTask = new SubTask("Подзадача новая", "Имеет дату, должна попасть в prioritizedList",
                LocalDateTime.of(2025, 10, 10, 12, 00),
                Duration.ofMinutes(600), 0);
        taskManager.addTask(newTask);

        // then
        Assertions.assertEquals(11, taskManager.prioritizedTasks.size());
        Assertions.assertTrue(taskManager.prioritizedTasks.contains(newTask));
    }

    @Test
    void addSubTask_notAddSubTaskToPrioritizedList_subTaskNotContainsStartTime() {
        // given
        initData();

        // when
        SubTask newTask = new SubTask("Подзадача новая", "Не имеет дату, не должна попасть в prioritizedList",
                null,
                Duration.ofMinutes(600), 0);
        taskManager.addTask(newTask);

        // then
        Assertions.assertEquals(10, taskManager.prioritizedTasks.size());
        Assertions.assertFalse(taskManager.prioritizedTasks.contains(newTask));
    }

    @Test
    void clearTasks_deleteTasksFromPrioritizedList() {

        // given
        initData();

        // when
        taskManager.clearTasks();

        // then
        taskManager.getPrioritizedTasks().forEach(task ->
                Assertions.assertNotEquals(TaskType.TASK, task.getType()));

    }

    @Test
    void clearSubTasks_deleteSubTasksFromPrioritizedList() {

        // given
        initData();

        // when
        taskManager.clearSubTasks();

        // then
        taskManager.getPrioritizedTasks().forEach(task ->
                Assertions.assertNotEquals(TaskType.SUBTASK, task.getType()));

    }

    @Test
    void clearEpics_deleteEpicsFromPrioritizedList() {

        // given
        initData();

        // when
        taskManager.clearEpics();

        // then
        taskManager.getPrioritizedTasks().forEach(task ->
                Assertions.assertNotEquals(TaskType.SUBTASK, task.getType()));

    }
}
