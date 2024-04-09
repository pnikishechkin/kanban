package ru.nikishechkin.kanban.manager.task;

import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private String fileName = "";
    private final String header = "id,type,name,status,description,epic";

    public FileBackedTaskManager(HistoryManager historyManager, String fileName) {
        super(historyManager);
        this.fileName = fileName;
    }

    private void save() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {

            bw.write(header);
            bw.newLine();

            for (Task task : getTasks()) {
                bw.write(task.toString());
                bw.newLine();
            }
            for (Epic epic : getEpics()) {
                bw.write(epic.toString());
                bw.newLine();
            }
            for (SubTask subTask : getSubTasks()) {
                bw.write(subTask.toString());
                bw.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла!", e);
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
     * @param fileName
     */
    public void loadFromFile(String fileName) {

        // Какой способ чтения файлов предпочтительнее? (современнее?) в описании ТЗ написано про метод
        // Files.readString(file.toPath()); - он имеет какие-то преимущества перед BufferedReader?

        try (BufferedReader bw = new BufferedReader(new FileReader(fileName))) {

            bw.readLine(); // хэдер

            while (bw.ready()) {
                String str = bw.readLine();
                Task newTask = getTaskFromString(str);

                if (newTask == null) {
                    // throw new ManagerLoadException("Ошибка загрузки из файла!");
                }

                switch (newTask.getType()) {
                    case EPIC:
                        addEpic((Epic)newTask);
                        break;
                    case TASK:
                        addTask(newTask);
                        break;
                    case SUBTASK:
                        addSubTask((SubTask)newTask);
                        break;
                }
            }

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка загрузки из файла!", e);
        }
    }

    private Task getTaskFromString(String str) {
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
}
