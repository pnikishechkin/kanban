package ru.nikishechkin.kanban;

import ru.nikishechkin.kanban.manager.TaskManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("----------------------------------------");
        System.out.println("------------- NIK-TASK v1.0 ------------");
        System.out.println("----------------------------------------");

        System.out.println();

        TaskManager taskManager = new TaskManager();
        addTestData(taskManager);
        printAllTasks(taskManager);

        System.out.println("ИЗМЕНЕНИЕ ДАННЫХ ЗАДАЧ, СТАТУСОВ И УДАЛЕНИЕ ЗАДАЧИ..." + "\n");
        changeStatus(taskManager);
        printAllTasks(taskManager);
    }

    /**
     * Создание тестовых данных эпиков, подзадач и задач
     *
     * @param taskManager менеджер задач
     */
    public static void addTestData(TaskManager taskManager) {

        taskManager.addEpic(new Epic("Эпик 1", "Описание"));

        taskManager.addSubTask(new SubTask("Подзадача 1_1", "Описание", 0));
        taskManager.addSubTask(new SubTask("Подзадача 1_2", "Описание", 0));
        taskManager.addSubTask(new SubTask("Подзадача 1_3", "Описание", 0));

        taskManager.addEpic(new Epic("Эпик 2", "Описание"));
        taskManager.addSubTask(new SubTask("Подзадача 2_1", "Описание", 4));
        taskManager.addSubTask(new SubTask("Подзадача 2_2", "Описание", 4));

        taskManager.addEpic(new Epic("Эпик 3", "Описание"));
        taskManager.addSubTask(new SubTask("Подзадача 3_1", "Описание", 7));
        taskManager.addSubTask(new SubTask("Подзадача 3_2", "Описание", 7));
        taskManager.addSubTask(new SubTask("Подзадача 3_3", "Описание", 7));

        taskManager.addTask(new Task("Задача 1", "Описание"));
        taskManager.addTask(new Task("Задача 2", "Описание"));
    }

    /**
     * Изменение статусов задач путем создания новых объектов и передачи их в ru.nikishechkin.kanban.controller.TaskManager
     *
     * @param taskManager менеджер задач
     */
    public static void changeStatus(TaskManager taskManager) {

        Epic epic1 = taskManager.getEpicById(0);
        epic1.setName("Эпик 1 изм");

        // Получаем задачи, которую необходимо изменить
        SubTask subTask1 = taskManager.getSubTaskById(1);
        SubTask subTask2 = taskManager.getSubTaskById(2);

        // Создаем новую подзадачу с таким же идентификатором, но измененными данными
        if (subTask1 != null) {

            SubTask updatedSubTask1 = new SubTask(subTask1.getId(),
                    "Подзадача 1_1 изм",
                    subTask1.getDescription(),
                    TaskStatus.DONE,
                    subTask1.getEpicId());

            taskManager.editSubTask(updatedSubTask1);
        }
        if (subTask2 != null) {
            SubTask updatedSubTask2 = new SubTask(subTask2.getId(),
                    "Подзадача 1_2 изм",
                    subTask2.getDescription(),
                    TaskStatus.DONE,
                    subTask2.getEpicId());

            taskManager.editSubTask(updatedSubTask2);
        }

        // Удаление 3 подзадачи в Эпике 1
        taskManager.deleteSubTask(3);

        SubTask subTask4 = taskManager.getSubTaskById(5);
        if (subTask4 != null) {
            SubTask updatedSubTask3 = new SubTask(subTask4.getId(),
                    "Подзадача 2_1 изм",
                    subTask4.getDescription(),
                    TaskStatus.IN_PROGRESS,
                    subTask4.getEpicId());

            taskManager.editSubTask(updatedSubTask3);
        }


        // Изменение задачи 1
        Task task1 = taskManager.getTaskById(11);
        Task updatedTask1 = new Task(task1.getId(),
                "Задача 1 изм",
                task1.getDescription(),
                TaskStatus.DONE);
        taskManager.editTask(updatedTask1);

        // Изменение задачи 2
        Task task2 = taskManager.getTaskById(12);
        Task updatedTask2 = new Task(task2.getId(),
                "Задача 2 изм",
                task1.getDescription(),
                TaskStatus.IN_PROGRESS);
        taskManager.editTask(updatedTask2);
    }

    /**
     * Вывод на экран всех эпиков, подзадач и задач
     *
     * @param taskManager менеджер задач
     */
    public static void printAllTasks(TaskManager taskManager) {

        ArrayList<Epic> epics = taskManager.getEpics();
        ArrayList<Task> tasks = taskManager.getTasks();

        System.out.println("НОВЫЕ ЗАДАЧИ:" + "\n");

        for (Epic epic : epics) {
            if (epic.getStatus() == TaskStatus.NEW) {
                System.out.println(epic);
                for (SubTask subTask : taskManager.getListEpicSubTasks(epic.getId())) {
                    System.out.println(subTask.toString());
                }
                System.out.println();
            }
        }
        for (Task task : tasks) {
            if (task.getStatus() == TaskStatus.NEW) {
                System.out.println(task);
            }
        }

        System.out.println("\n\n" + "В РАБОТЕ:" + "\n");

        for (Epic epic : epics) {
            if (epic.getStatus() == TaskStatus.IN_PROGRESS) {
                System.out.println(epic);
                for (SubTask subTask : taskManager.getListEpicSubTasks(epic.getId())) {
                    System.out.println(subTask.toString());
                }
                System.out.println();
            }
        }
        for (Task task : tasks) {
            if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                System.out.println(task);
            }
        }

        System.out.println("\n" + "ЗАВЕРШЕННЫЕ:" + "\n");

        for (Epic epic : epics) {
            if (epic.getStatus() == TaskStatus.DONE) {
                System.out.println(epic);
                for (SubTask subTask : taskManager.getListEpicSubTasks(epic.getId())) {
                    System.out.println(subTask.toString());
                }
                System.out.println();
            }
        }
        for (Task task : tasks) {
            if (task.getStatus() == TaskStatus.DONE) {
                System.out.println(task);
            }
        }
    }
}
