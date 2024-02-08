package ru.nikishechkin.kanban.manager;

import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private HashMap<Integer, Task> tasks;

    private static int count = 0;

    public TaskManager() {
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.tasks = new HashMap<>();
    }

    /**
     * Получить коллекцию, содержащую все созданные эпики
     *
     * @return коллекция эпиков
     */
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Получить коллекцию, содержащую все созданные подзадачи
     *
     * @return коллекция подзадач
     */
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**
     * Получить коллекцию, содержащую все созданные задачи (самостоятельные)
     *
     * @return коллекция задач
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Очистить все эпики
     */
    public void clearEpics() {
        epics.clear();
        // Подзадачи также удаляются, поскольку не имеют самостоятельного значения
        subTasks.clear();
    }

    /**
     * Очистить все эпики
     */
    public void clearSubTasks() {
        subTasks.clear();
        // Очищаем все списки подзадач у эпиков, поскольку их больше нет
        for (Epic epic : getEpics()) {
            epic.getSubTasksIds().clear();
        }
    }

    /**
     * Очистить все эпики
     */
    public void clearTasks() {
        tasks.clear();
    }

    /**
     * Получить эпик по переданному идентификатору
     *
     * @param id идентификатор эпика
     * @return объект эпика
     */
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    /**
     * Получить подзадачу по переданному идентификатору
     *
     * @param id идентификатор подзадачи
     * @return объект подзадачи
     */
    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    /**
     * Получить задачу по переданному идентификатору
     *
     * @param id идентификатор задачи
     * @return объект задачи
     */
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    /**
     * Добавление нового эпика
     *
     * @param epic объект класса эпика
     */
    public void addEpic(Epic epic) {

        if (epic == null) {
            return;
        }

        // Назначение идентификатора
        if (epic.getId() == null) {
            epic.setId(count);
            count++;
        } else {
            if (epics.containsKey(epic.getId())) {
                System.out.println("Ошибка добавления нового эпика! Эпик с таким идентификатором уже существует!");
            }
        }

        epics.put(epic.getId(), epic);
    }

    /**
     * Добавление новой подзадачи
     *
     * @param subTask объект класса подзадачи
     */
    public void addSubTask(SubTask subTask) {

        if (subTask == null) {
            return;
        }

        // Назначение идентификатора
        if (subTask.getId() == null) {
            subTask.setId(count);
            count++;
        } else {
            if (subTasks.containsKey(subTask.getId())) {
                System.out.println("Ошибка добавления новой подзадачи! Подзадача с таким идентификатором уже существует!");
            }
        }

        subTasks.put(subTask.getId(), subTask);

        // Добавление подзадачи в список подзадач связанного эпика
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTask(subTask.getId());
        } else {
            System.out.println("Ошибка! У подзадачи с идентификатором " + subTask.getId() + "не найден эпик " +
                    "с идентификатором" + subTask.getEpicId());
        }


        // Обновление статуса связанного эпика
        updateEpicStatus(subTask.getEpicId());
    }

    /**
     * Добавление новой задачи
     *
     * @param task объект класса задачи
     */
    public void addTask(Task task) {

        if (task == null) {
            return;
        }

        // Назначение идентификатора
        if (task.getId() == null) {
            task.setId(count);
            count++;
        } else {
            if (tasks.containsKey(task.getId())) {
                System.out.println("Ошибка добавления новой задачи! Задача с таким идентификатором уже существует!");
            }
        }

        tasks.put(task.getId(), task);
    }

    /**
     * Редактирование эпика (реализуется путем передачи нового объекта)
     *
     * @param epic объект эпика
     */
    public void editEpic(Epic epic) {
        this.epics.put(epic.getId(), epic);
    }

    /**
     * Редактирование подзадачи (реализуется путем передачи нового объекта)
     *
     * @param subTask объект подзадачи
     */
    public void editSubTask(SubTask subTask) {

        Integer subTaskId = subTask.getId();
        subTasks.put(subTaskId, subTask);

        // Проверка, что у подзадачи изменился связанный с ней эпик
        // Если связанный эпик не содержит в своем списке идентификаторов подзадач данной подзадачи
        Epic epic = epics.get(subTask.getEpicId());
        if (!epic.getSubTasksIds().contains(subTaskId)) {

            // Находим, к какому эпику принадлежала ранее данная подзадача и удаляем ее из коллекции
            for (Epic ep : epics.values()) {
                ep.getSubTasksIds().remove(subTaskId);
            }

            epic.addSubTask(subTaskId);
        }

        this.updateEpicStatus(subTask.getEpicId());
    }

    /**
     * Редактирование задачи (реализуется путем передачи нового объекта)
     *
     * @param task объект задачи
     */
    public void editTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Удаление эпика по идентификатору
     *
     * @param id идентификатор эпика
     */
    public void deleteEpic(int id) {

        Epic epic = this.epics.get(id);
        if (epic == null) {
            return;
        }

        // Удаление входящих в эпик подзадач
        for (Integer subTaskId : epic.getSubTasksIds()) {
            subTasks.remove(subTaskId);
        }

        // Удаление переданной задачи
        epics.remove(id);
    }

    /**
     * Удаление подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     */
    public void deleteSubTask(int id) {

        SubTask subTask = subTasks.get(id);

        if (subTask == null) {
            return;
        }

        // Удаление подзадачи у связанного с ней эпика
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.getSubTasksIds().remove(id);
        }

        // Обновление статуса эпика после удаления одной из подзадач
        this.updateEpicStatus(subTask.getEpicId());

        // Удаление подзадачи из общей коллекции
        this.subTasks.remove(id);
    }

    /**
     * Удалить задачу по переданному идентификатору
     *
     * @param id идентификатор задачи
     */
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    /**
     * Получить список подзадач эпика
     *
     * @param epicId идентификатор эпика
     * @return список подзадач
     */
    public ArrayList<SubTask> getListEpicSubTasks(int epicId) {
        Epic epic = this.epics.get(epicId);
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();

        if (epic == null) {
            return null;
        }

        for (Integer subTaskId : epic.getSubTasksIds()) {
            epicSubTasks.add(subTasks.get(subTaskId));
        }
        return epicSubTasks;
    }

    /**
     * Обновление статуса эпика на основе статусов подзадач, входящих в него
     */
    private void updateEpicStatus(Integer epicId) {

        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        if (epic.getSubTasksIds().size() == 0) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        // Флаг, сигнализирующий о том, что все задачи эпика являются новыми
        boolean allNew = true;

        // Флаг, сигнализирующий о том, что все задачи эпика являются решенными
        boolean allDone = true;

        for (Integer subTaskId : epic.getSubTasksIds()) {
            // Если хотя бы у одной задачи статус не Новой, снимаем флаг
            if (subTasks.get(subTaskId).getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            // Если хотя бы у одной задачи статус не решенной, снимаем флаг
            else if (subTasks.get(subTaskId).getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
