package ru.nikishechkin.kanban.manager.task;

import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class TaskConverterCsv {

    public static final String header = "id,type,name,status,description,epic";

    /**
     * Сконвертировать задачу в csv строку
     * @param task
     * @return
     */
    public static String taskToCsvLine(Task task) {

        StringBuilder csv = new StringBuilder(task.getId() + "," + task.getType().toString() + "," + task.getName() + "," +
                task.getStatus().toString() + "," + task.getDescription());

        if (task instanceof SubTask) {
            csv.append("," + ((SubTask) task).getEpicId());
        }
        csv.append("\n");

        return csv.toString();
    }

    /**
     * Получить csv строку для записи в файл с списком идентификаторов истории просмотров задач
     * @param historyManager
     * @return
     */
    public static String historyToCsvLine(HistoryManager historyManager) {

        StringBuilder csv = new StringBuilder();

        // Запись истории в csv строку
        for (Task task : historyManager.getHistory()) {
            csv.append(task.getId().toString());
            csv.append(",");
        }

        return csv.toString();
    }

    /**
     * Сконвертировать csv строку в задачу
     * @param csv
     * @return
     */
    public static Task csvLineToTask(String csv) {

        if (csv == null || csv.isEmpty())
            return null;

        String[] data = csv.split(",");
        Task task = null;

        if (data.length == 5 || data.length == 6) {
            switch (data[1]) {
                case "EPIC":
                    task = new Epic(data[2], data[4]);
                    break;
                case "TASK":
                    task = new Task(data[2], data[4]);
                    break;
                case "SUBTASK":
                    task = new SubTask(data[2], data[4], Integer.parseInt(data[5]));
                    break;
            }
            task.setId(Integer.parseInt(data[0]));

            TaskStatus status = TaskStatus.valueOf(data[3]);
            task.setStatus(status);
        }

        return task;
    }

    /**
     * Получить список идентификаторов истории просмотров задач на основе строки из файла
     * @param csv
     * @return
     */
    public static List<Integer> сsvLineToIds(String csv) {

        List<Integer> res = new ArrayList<>();
        String[] val = csv.split(",");

        for (String v : val) {
            res.add(Integer.parseInt(v));
        }

        return res;
    }

}
