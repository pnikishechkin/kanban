package ru.nikishechkin.kanban;

import ru.nikishechkin.kanban.manager.Managers;
import ru.nikishechkin.kanban.manager.task.TaskManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("----------------------------------------");
        System.out.println("------------- NIK-TASK v 2.0 ------------");
        System.out.println("----------------------------------------");
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
