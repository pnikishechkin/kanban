package ru.nikishechkin.kanban.manager.task;

import ru.nikishechkin.kanban.exceptions.ManagerFileException;
import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private String fileName = "";

    private FileBackedTaskManager(HistoryManager historyManager, String fileName) {
        super(historyManager);
        this.fileName = fileName;
    }

    /**
     * Метод для создания экземпляра класса файлового таск-менеджера и загрузки из файла
     * @param historyManager
     * @param fileName
     * @return
     */
    public static FileBackedTaskManager loadFromFile(HistoryManager historyManager, String fileName) {

        FileBackedTaskManager fbtm = new FileBackedTaskManager(historyManager, fileName);
        fbtm.load();
        return fbtm;
    }

    private void save() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {

            StringBuilder res = new StringBuilder();

            // Заголовок
            res.append(TaskConverterCsv.header);
            res.append("\n");

            // Запись задач, эпиков, подзадач
            for (Task task : this.getTasks()) {
                res.append(TaskConverterCsv.taskToCsvLine(task));
            }
            for (Epic epic : this.getEpics()) {
                res.append(TaskConverterCsv.taskToCsvLine(epic));
            }
            for (SubTask subTask : this.getSubTasks()) {
                res.append(TaskConverterCsv.taskToCsvLine(subTask));
            }

            // Дополнительная пустая строка перед записью истории
            res.append("\n");

            // Запись истории
            res.append(TaskConverterCsv.historyToCsvLine(historyManager));

            bw.write(res.toString());

        } catch (IOException e) {
            throw new ManagerFileException("Ошибка при сохранении файла!", e);
        }
    }

    /**
     * Загрузить данные из внешнего файла
     */
    private void load() {
        try {
            // Считывание всех данных файла в строку
            String str = Files.readString(Paths.get(fileName));

            if (str == null || str.isEmpty())
                return;

            Boolean wasEmpty = false;

            // Считывание задач, эпиков и подзадач
            for (String s: str.lines().collect(Collectors.toList())) {

                if (s.equals(TaskConverterCsv.header)) continue;

                if (!s.isEmpty() && !wasEmpty) {
                    // Считывание задач
                    Task newTask = TaskConverterCsv.csvLineToTask(s);

                    switch (newTask.getType()) {
                        case EPIC:
                            this.addEpic((Epic) newTask);
                            break;
                        case TASK:
                            this.addTask(newTask);
                            break;
                        case SUBTASK:
                            this.addSubTask((SubTask) newTask);
                            break;
                    }
                } else if (!s.isEmpty() && wasEmpty) { // Считывание истории

                    List<Integer> ids = TaskConverterCsv.сsvLineToIds(s);

                    for (Integer id : ids) {

                        Task task = this.tasks.get(id);
                        if (task == null) {
                            task = this.epics.get(id);
                        }
                        if (task == null) {
                            task = this.subTasks.get(id);
                        }
                        if (task == null) {
                            throw new ManagerFileException("Ошибка чтения истории задач - задача с идентификатором "
                                    + id.toString() + " не найдена");
                        }
                        historyManager.add(task);
                    }

                } else {
                    wasEmpty = true;
                }
            }

            // Поиск максимального значения идентификатора задач и его назначение
            idCounter = Math.max(Math.max(Collections.max(epics.keySet()), Collections.max(subTasks.keySet())),
                     Collections.max(tasks.keySet())) + 1;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void editEpic(Epic epic) {
        super.editEpic(epic);
        save();
    }

    @Override
    public void editSubTask(SubTask subTask) {
        super.editSubTask(subTask);
        save();
    }

    @Override
    public void editTask(Task task) {
        super.editTask(task);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }
}
