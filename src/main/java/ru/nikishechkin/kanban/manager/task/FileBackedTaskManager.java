package ru.nikishechkin.kanban.manager.task;

import ru.nikishechkin.kanban.exceptions.ManagerFileException;
import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private String fileName = "";

    public FileBackedTaskManager(HistoryManager historyManager, String fileName) {
        super(historyManager);
        this.fileName = fileName;
    }

    private void save() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {

            String str = TaskConverterCsv.getStrFromTasksAndHistory(this, historyManager);
            bw.write(str);

        } catch (IOException e) {
            throw new ManagerFileException("Ошибка при сохранении файла!", e);
        }
    }

    /**
     * Загрузить данные из привязанного файла
     */
    public void load() {
        this.loadFromFile(fileName);
    }

    /**
     * Загрузить данные из внешнего файла (без привязки к нему)
     *
     * @param fileName
     */
    public void loadFromFile(String fileName) {

        try {
            String str = Files.readString(Paths.get(fileName));
            this.clearAll();
            TaskConverterCsv.getTasksAndHistoryFromStr(str, this, historyManager);
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
