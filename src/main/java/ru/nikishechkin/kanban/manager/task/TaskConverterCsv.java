package ru.nikishechkin.kanban.manager.task;

import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskConverterCsv {

    public static final String header = "id,type,name,status,description,startTime,duration,epic";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd;HH:mm");
    private static final String emptyDate = "-";

    /**
     * Сконвертировать задачу в csv строку
     *
     * @param task
     * @return
     */
    public static String taskToCsvLine(Task task) {

        StringBuilder csv = new StringBuilder(task.getId() + "," + task.getType().toString() + "," + task.getName() + "," +
                task.getStatus().toString() + "," + task.getDescription() + "," +
                (task.getStartTime().isPresent() ? task.getStartTime().get().format(formatter) : emptyDate) + "," +
                task.getDuration().toMinutes());

        if (task instanceof SubTask) {
            csv.append("," + ((SubTask) task).getEpicId());
        }
        csv.append("\n");

        return csv.toString();
    }

    /**
     * Получить csv строку для записи в файл с списком идентификаторов истории просмотров задач
     *
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
     *
     * @param csv
     * @return
     */
    public static Task csvLineToTask(String csv) {

        if (csv == null || csv.isEmpty())
            return null;

        String[] data = csv.split(",");
        Task task = null;

        LocalDateTime startTime = null;


        switch (data[1]) {
            case "EPIC":
                task = new Epic(data[2], data[4]);
                break;
            case "TASK":
                if (!data[5].equals(emptyDate)) {
                    startTime = LocalDateTime.parse(data[5], formatter);
                }
                task = new Task(data[2], data[4], startTime, Duration.ofMinutes(Integer.parseInt(data[6])));
                break;
            case "SUBTASK":
                if (!data[5].equals(emptyDate)) {
                    startTime = LocalDateTime.parse(data[5], formatter);
                }
                task = new SubTask(data[2], data[4], startTime, Duration.ofMinutes(Integer.parseInt(data[6])),
                        Integer.parseInt(data[7]));
                break;
        }
        task.setId(Integer.parseInt(data[0]));

        TaskStatus status = TaskStatus.valueOf(data[3]);
        task.setStatus(status);

        return task;
    }

    /**
     * Получить список идентификаторов истории просмотров задач на основе строки из файла
     *
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
