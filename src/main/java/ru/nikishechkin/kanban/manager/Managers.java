package ru.nikishechkin.kanban.manager;

import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.manager.history.InMemoryHistoryManager;
import ru.nikishechkin.kanban.manager.task.FileBackedTaskManager;
import ru.nikishechkin.kanban.manager.task.InMemoryTaskManager;
import ru.nikishechkin.kanban.manager.task.TaskManager;
import ru.nikishechkin.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class Managers {

    private static HistoryManager historyManager;
    private static TaskManager taskManager;

    private Managers() { }

    public static TaskManager getDefault() {

        if (historyManager == null) {
            getDefaultHistory();
        }
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager(historyManager);
        }

        return taskManager;
    }

    public static FileBackedTaskManager getFileBackedTaskManager() {

        if (historyManager == null) {
            getDefaultHistory();
        }

        FileBackedTaskManager fbtm = new FileBackedTaskManager(historyManager, "resources\\tasks.csv");
        fbtm.load();
        return fbtm;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }

    // Не совсем понял про применение методов ниже, связанных с сохранением/загрузкой данных по истории просмотра в
    // файл. Мысли такие, что эти данные следует сохранить в отдельный файл, или в этом файле отдельной строкой с какой-то
    // меткой записать перечень идентификаторов задач в соответствии с историей просмотра. Или это не требуется?

    public static List<Integer> historyFromString(String str) {
        String[] ids = str.split(" ");
        List<Integer> res = new ArrayList<>();

        for (int i = 0; i < ids.length; i++) {
            res.add(Integer.parseInt(ids[i]));
        }

        return res;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder res = new StringBuilder("");
        for (Task task : manager.getHistory()) {
             res.append(task.getId() + " ");
        }
        return res.toString();
    }
}
