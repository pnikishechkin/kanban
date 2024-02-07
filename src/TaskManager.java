import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private HashMap<Integer, Task> tasks;

    public TaskManager() {
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.tasks = new HashMap<>();
    }

    /**
     * Получить коллекцию, содержащую все созданные эпики
     * @return коллекция эпиков
     */
    public Collection<Epic> getEpics() {
        return epics.values();
    }

    /**
     * Получить коллекцию, содержащую все созданные подзадачи
     * @return коллекция подзадач
     */
    public Collection<SubTask> getSubTasks() {
        return subTasks.values();
    }

    /**
     * Получить коллекцию, содержащую все созданные задачи (самостоятельные)
     * @return коллекция задач
     */
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    /**
     * Очистить все задачи (каждый из списков)
     */
    public void clearAllTasks() {
        this.tasks.clear();
        this.subTasks.clear();
        this.epics.clear();
    }

    /**
     * Получить эпик по переданному идентификатору
     * @param id идентификатор эпика
     * @return объект эпика
     */
    public Epic getEpicById(int id) {
        return this.epics.get(id);
    }

    /**
     * Получить подзадачу по переданному идентификатору
     * @param id идентификатор подзадачи
     * @return объект подзадачи
     */
    public SubTask getSubTaskById(int id) {
        return this.subTasks.get(id);
    }

    /**
     * Получить задачу по переданному идентификатору
     * @param id идентификатор задачи
     * @return объект задачи
     */
    public Task getTaskById(int id) {
        return this.tasks.get(id);
    }

    /**
     * Добавление задачи/подзадачи/эпика.
     * Реализовано в виде одного метода во избежание дублирования кода
     *
     * @param task объект класса задачи
     */
    public void addTask(Task task) {

        if (task == null) {
            return;
        }

        // В зависимости от типа задачи, производится добавление в соответствующую коллекцию
        if (task instanceof Epic) {
            this.epics.put(task.getId(), (Epic) task);
        } else if (task instanceof SubTask) {
            SubTask newSubTask = (SubTask) task;
            this.subTasks.put(task.getId(), newSubTask);
        } else if (task instanceof Task) {
            this.tasks.put(task.getId(), (Task) task);
        }
    }

    /**
     * Редактирование задачи (реализуется путем передачи нового объекта)
     *
     * @param editedTask
     */
    public void editTask(Task editedTask) {
        this.addTask(editedTask);

        if(editedTask instanceof SubTask) {
            ((SubTask) editedTask).getEpic().checkStatus();
        }
    }

    /**
     * Удаление эпика по идентификатору
     *
     * @param id
     */
    public void deleteEpic(int id) {

        Epic epic = this.epics.get(id);
        if (epic == null) {
            return;
        }

        // Удаление входящих в эпик подзадач
        for (SubTask subTask : epic.getListSubTasks()) {
            this.subTasks.remove(subTask.getId());
        }
        epic.getListSubTasks().clear();

        // Удаление переданной задачи
        this.epics.remove(id);
    }

    /**
     * Удаление подзадачи по идентификатору
     *
     * @param id
     */
    public void deleteSubTask(int id) {

        SubTask subTask = this.subTasks.get(id);

        if (subTask == null) {
            return;
        }

        // Удаление подзадачи у связанного с ней эпика
        subTask.getEpic().getListSubTasks().remove(subTask);

        // Проверка статуса эпика после удаления одной из подзадач
        subTask.getEpic().checkStatus();

        // Удаление подзадачи из общей коллекции
        this.subTasks.remove(id);
    }

    /**
     * Удалить задачу по переданному идентификатору
     * @param id идентификатор задачи
     */
    public void deleteTask(int id) {
        this.tasks.remove(id);
    }

    /**
     * Получить список подзадач эпика
     *
     * @param epicId идентификатор эпика
     * @return список подзадач
     */
    public ArrayList<SubTask> getListSubTasks(int epicId) {
        Epic epic = this.epics.get(epicId);

        if (epic == null) {
            return null;
        }

        if (epic instanceof Epic) {
            return ((Epic) epic).getListSubTasks();
        } else {
            return null;
        }
    }
}
