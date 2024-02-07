public class Main {

    public static void main(String[] args) {
        System.out.println("--- NIK-TASK ---");

        System.out.println();

        TaskManager taskManager = new TaskManager();
    }

    public static void addTestData(TaskManager taskManager) {
        Epic epic1 = new Epic("Большая задача", "Описание");
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание", epic1);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание", epic1);
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание", epic1);

        Epic epic2 = new Epic("Другая большая задача", "Описание");
        SubTask subTask4 = new SubTask("Подзадача 2_1", "Описание", epic2);
        SubTask subTask5 = new SubTask("Подзадача 2_2", "Описание", epic2);

        Epic epic3 = new Epic("Большая задача", "Описание");
        SubTask subTask6 = new SubTask("Подзадача 3_1", "Описание", epic3);
        SubTask subTask7 = new SubTask("Подзадача 3_2", "Описание", epic3);
        SubTask subTask8 = new SubTask("Подзадача 3_3", "Описание", epic3);

        Task task1 = new Task("Самостоятельная задача 1", "Описание");
        Task task2 = new Task("Самостоятельная задача 2", "Описание");

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

    public static void printAllTasks(TaskManager taskManager) {

    }
}
