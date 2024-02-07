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

        System.out.println("ИЗМЕНЕНИЕ ДАННЫХ ЗАДАЧ, СТАТУСОВ И УДАЛЕНИЕ ЗАДАЧИ..." +"\n");
        changeStatus(taskManager);
        printAllTasks(taskManager);
    }

    /**
     * Создание тестовых данных эпиков, подзадач и задач
     * @param taskManager
     */
    public static void addTestData(TaskManager taskManager) {
        Epic epic1 = new Epic("Эпик 1", "Описание");
        SubTask subTask1 = new SubTask("Подзадача 1_1", "Описание", epic1);
        SubTask subTask2 = new SubTask("Подзадача 1_2", "Описание", epic1);
        SubTask subTask3 = new SubTask("Подзадача 1_3", "Описание", epic1);

        Epic epic2 = new Epic("Эпик 2", "Описание");
        SubTask subTask4 = new SubTask("Подзадача 2_1", "Описание", epic2);
        SubTask subTask5 = new SubTask("Подзадача 2_2", "Описание", epic2);

        Epic epic3 = new Epic("Эпик 3", "Описание");
        SubTask subTask6 = new SubTask("Подзадача 3_1", "Описание", epic3);
        SubTask subTask7 = new SubTask("Подзадача 3_2", "Описание", epic3);
        SubTask subTask8 = new SubTask("Подзадача 3_3", "Описание", epic3);

        Task task1 = new Task("Задача 1", "Описание");
        Task task2 = new Task("Задача 2", "Описание");

        taskManager.addTask(epic1);
        taskManager.addTask(epic2);
        taskManager.addTask(epic3);

        taskManager.addTask(subTask1);
        taskManager.addTask(subTask2);
        taskManager.addTask(subTask3);
        taskManager.addTask(subTask4);
        taskManager.addTask(subTask5);
        taskManager.addTask(subTask6);
        taskManager.addTask(subTask7);
        taskManager.addTask(subTask8);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
    }

    /**
     * Изменение статусов задач путем создания новых объектов и передачи их в TaskManager
     * @param taskManager
     */
    public static void changeStatus(TaskManager taskManager) {

        // Получаем задачи, которую необходимо изменить
        SubTask subTask1 = taskManager.getSubTaskById(1);
        SubTask subTask2 = taskManager.getSubTaskById(2);

        // Создаем новую подзадачу с таким же идентификатором, но измененными данными
        if (subTask1 != null) {

        /*
        Вопрос ревьюеру, правильно я понял, что мы должны действовать именно так:
        создать новый объект задачи с измененными данными задачи, и вызвать метод обновления задачи в менеджере,
        а не просто использовать метод setTaskStatus и просто обновить статус у задачи?

        То есть статус задачи можно задать только при создании нового объекта, верно?
        Если так, то не совсем понятна причина, почему нельзя использовать метод изменения статуса задачи напрямую.

        ИЗ ОПИСАНИЯ:
        "Фраза «информация приходит вместе с информацией по задаче» означает, что не существует отдельного метода,
        который занимался бы только обновлением статуса задачи. Вместо этого статус задачи обновляется вместе с
        полным обновлением задачи."

        "Измените статусы созданных объектов, распечатайте их."

        Изменить путем создания нового объекта? Или все-таки можем использовать функцию изменения статуса задачи напрямую?
         */

            SubTask updatedSubTask1 = new SubTask(subTask1.getId(),
                    "Подзадача 1_1 изм",
                    subTask1.getDescription(),
                    TaskStatus.DONE,
                    subTask1.getEpic());

            taskManager.editTask(updatedSubTask1);
        }
        if (subTask2 != null) {
            SubTask updatedSubTask2 = new SubTask(subTask2.getId(),
                    "Подзадача 1_2 изм",
                    subTask2.getDescription(),
                    TaskStatus.DONE,
                    subTask2.getEpic());

            taskManager.editTask(updatedSubTask2);
        }

        // Удаление 3 подзадачи в Эпике 1
        taskManager.deleteSubTask(3);

        SubTask subTask4 = taskManager.getSubTaskById(5);
        if (subTask4 != null) {
            SubTask updatedSubTask3 = new SubTask(subTask4.getId(),
                    "Подзадача 2_1 изм",
                    subTask4.getDescription(),
                    TaskStatus.IN_PROGRESS,
                    subTask4.getEpic());

            taskManager.editTask(updatedSubTask3);
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
     * @param taskManager
     */
    public static void printAllTasks(TaskManager taskManager) {

        Collection<Epic> epics = taskManager.getEpics();
        Collection<Task> tasks = taskManager.getTasks();

        System.out.println("НОВЫЕ ЗАДАЧИ:" + "\n");

        for (Epic epic : epics) {
            if (epic.getTaskStatus() == TaskStatus.NEW) {
                System.out.println(epic);
            }
        }
        for (Task task : tasks) {
            if (task.getTaskStatus() == TaskStatus.NEW) {
                System.out.println(task);
            }
        }

        System.out.println("\n\n" + "В РАБОТЕ:" + "\n");

        for (Epic epic : epics) {
            if (epic.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                System.out.println(epic);
            }
        }
        for (Task task : tasks) {
            if (task.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                System.out.println(task);
            }
        }

        System.out.println("\n\n" + "ЗАВЕРШЕННЫЕ:" + "\n");

        for (Epic epic : epics) {
            if (epic.getTaskStatus() == TaskStatus.DONE) {
                System.out.println(epic);
            }
        }
        for (Task task : tasks) {
            if (task.getTaskStatus() == TaskStatus.DONE) {
                System.out.println(task);
            }
        }
    }
}
