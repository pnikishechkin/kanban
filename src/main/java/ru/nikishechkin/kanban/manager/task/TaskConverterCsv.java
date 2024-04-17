package ru.nikishechkin.kanban.manager.task;

import ru.nikishechkin.kanban.exceptions.ManagerFileException;
import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.util.stream.Collectors;

public class TaskConverterCsv {

    public static String getStrFromTasksAndHistory(TaskManager taskManager, HistoryManager historyManager) {

        StringBuilder res = new StringBuilder();

        // Заголовок
        res.append(header);
        res.append("\n");

        // Запись эпиков, подзадач, задач
        for (Task task : taskManager.getTasks()) {
            res.append(getCsvFromTask(task));
            res.append("\n");
        }
        for (Epic epic : taskManager.getEpics()) {
            res.append(getCsvFromTask(epic));
            res.append("\n");
        }
        for (SubTask subTask : taskManager.getSubTasks()) {
            res.append(getCsvFromTask(subTask));
            res.append("\n");
        }

        // Пустая строка перед записью истории
        res.append("\n");

        // Запись истории
        res.append(getIdsStrFromHistory(historyManager));

        return res.toString();
    }

    public static void getTasksAndHistoryFromStr(String str, TaskManager taskManager, HistoryManager historyManager) {

        if (str == null || str.isEmpty())
            return;

        Boolean wasEmpty = false;

        for (String s: str.lines().collect(Collectors.toList())) {

            if (s.equals(header)) continue;

            if (!s.isEmpty() && !wasEmpty) {
                // Считывание задач
                Task newTask = TaskConverterCsv.getTaskFromCsv(s);

                switch (newTask.getType()) {
                    case EPIC:
                        taskManager.addEpic((Epic) newTask);
                        break;
                    case TASK:
                        taskManager.addTask(newTask);
                        break;
                    case SUBTASK:
                        taskManager.addSubTask((SubTask) newTask);
                        break;
                }
            } else if (!s.isEmpty() && wasEmpty) {
                // Заполнение истории
                TaskConverterCsv.getHistoryFromIdsStr(s, historyManager, (InMemoryTaskManager) taskManager);
            } else {
                wasEmpty = true;
            }
        }
    }

    private static final String header = "id,type,name,status,description,epic";

    private static String getCsvFromTask(Task task) {

        StringBuilder csv = new StringBuilder(task.getId() + "," + task.getType().toString() + "," + task.getName() + "," +
                task.getStatus().toString() + "," + task.getDescription());

        if (task instanceof SubTask) {
            csv.append("," + ((SubTask) task).getEpicId());
        }

        return csv.toString();
    }

    private static Task getTaskFromCsv(String str) {

        if (str == null || str.isEmpty())
            return null;

        String[] data = str.split(",");
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

    private static void getHistoryFromIdsStr(String ids, HistoryManager historyManager, InMemoryTaskManager taskManager) {
        String[] val = ids.split(",");

        for (String v : val) {

            Integer id = Integer.parseInt(v);

            Task task = taskManager.tasks.get(id);
            if (task == null) {
                task = taskManager.epics.get(id);
            }
            if (task == null) {
                task = taskManager.subTasks.get(id);
            }

            if (task == null) {
                throw new ManagerFileException("Ошибка чтения истории задач - задача с идентификатором "
                        + id.toString() + " не найдена");
            }

            historyManager.add(task);
        }
    }

    private static String getIdsStrFromHistory(HistoryManager historyManager) {

        StringBuilder res = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            res.append(task.getId().toString());
            res.append(",");
        }
        return res.toString();
    }

}
