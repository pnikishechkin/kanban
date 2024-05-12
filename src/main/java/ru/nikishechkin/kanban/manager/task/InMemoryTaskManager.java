package ru.nikishechkin.kanban.manager.task;

import ru.nikishechkin.kanban.manager.history.HistoryManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subTasks;
    protected HashMap<Integer, Task> tasks;

    protected TreeSet<Task> prioritizedTasks;

    protected static int idCounter = 0;
    protected HistoryManager historyManager;

    Comparator<Task> taskComparatorByDate = new Comparator<>() {
        @Override
        public int compare(Task task1, Task task2) {
            if (task1.getStartTime().isPresent() && task2.getStartTime().isPresent()) {
                return task1.getStartTime().get().compareTo(task2.getStartTime().get());
            }
            return 1;
        }
    };

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.tasks = new HashMap<>();

        this.prioritizedTasks = new TreeSet<>(taskComparatorByDate);
        this.historyManager = historyManager;
    }
/*
    class TaskComparator implements Comparator<Task> {

        @Override
        public int compare(Task task1, Task task2) {
            if (task1.getStartTime().isPresent() && task2.getStartTime().isPresent()) {
                return task1.getStartTime().get().compareTo(task2.getStartTime().get());
            }
            return 1;
        }
    }
*/

    /**
     * Получить множество, содержащее задачи, имеющие дату начала и отсортированные по ней
     *
     * @return
     */
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    /**
     * Получить коллекцию, содержащую все созданные эпики
     *
     * @return коллекция эпиков
     */
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Получить коллекцию, содержащую все созданные подзадачи
     *
     * @return коллекция подзадач
     */
    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**
     * Получить коллекцию, содержащую все созданные задачи (самостоятельные)
     *
     * @return коллекция задач
     */
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Очистить все эпики
     */
    @Override
    public void clearEpics() {
        epics.clear();
        // Подзадачи также удаляются, поскольку не имеют самостоятельного значения
        clearSubTasks();
    }

    /**
     * Очистить все подзадачи
     */
    @Override
    public void clearSubTasks() {
        subTasks.values().forEach(subTask -> prioritizedTasks.remove(subTask));
        subTasks.clear();
        // Очищаем все списки подзадач у эпиков, поскольку их больше нет и обновляем их статусы
        getEpics().stream().forEach(epic -> {
            epic.getSubTasksIds().clear();
            updateEpicData(epic.getId());
        });
    }

    /**
     * Очистить все эпики
     */
    @Override
    public void clearTasks() {
        tasks.values().forEach(task -> prioritizedTasks.remove(task));
        tasks.clear();
    }

    /**
     * Получить эпик по переданному идентификатору
     *
     * @param id идентификатор эпика
     * @return объект эпика
     */
    @Override
    public Epic getEpicById(int id) {
        this.historyManager.add(epics.get(id));
        return epics.get(id);
    }


    /**
     * Получить подзадачу по переданному идентификатору
     *
     * @param id идентификатор подзадачи
     * @return объект подзадачи
     */
    @Override
    public SubTask getSubTaskById(int id) {
        this.historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    /**
     * Получить задачу по переданному идентификатору
     *
     * @param id идентификатор задачи
     * @return объект задачи
     */
    @Override
    public Task getTaskById(int id) {
        this.historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    /**
     * Добавление нового эпика
     *
     * @param epic объект класса эпика
     */
    @Override
    public void addEpic(Epic epic) {

        if (epic == null) {
            return;
        }

        // Назначение идентификатора
        if (epic.getId() == null) {
            epic.setId(idCounter);
            idCounter++;
        } else {
            if (epics.containsKey(epic.getId())) {
                System.out.println("Ошибка добавления нового эпика! Эпик с таким идентификатором уже существует!");
            }
        }

        epics.put(epic.getId(), epic);
    }


    /**
     * Обновление задачи в отсортированном множестве: если задача имеет дату начала, то добавить в множество.
     * Если дата начала не задана, то удалить из множества
     *
     * @param task
     */
    private void updateTaskInSortedSet(Task task) {
        if (task.getStartTime().isPresent()) {
            prioritizedTasks.add(task);
        } else {
            prioritizedTasks.remove(task);
        }
    }

    /**
     * Добавление новой подзадачи
     *
     * @param subTask объект класса подзадачи
     */
    @Override
    public void addSubTask(SubTask subTask) {

        if (subTask == null) {
            return;
        }

        // Проверка на пересечение с другими задачами
        try {
            checkTaskOverlapToExist(subTask);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        // Поиск связанного с сабтаской эпика
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            System.out.println("Ошибка! У подзадачи с идентификатором " + subTask.getId() + "не найден эпик " +
                    "с идентификатором" + subTask.getEpicId());
            return;
        }

        // Назначение сабтаске идентификатора
        if (subTask.getId() == null) {
            subTask.setId(idCounter);
            idCounter++;
        } else {
            if (subTasks.containsKey(subTask.getId())) {
                System.out.println("Ошибка добавления новой подзадачи! Подзадача с таким идентификатором уже существует!");
                return;
            }
        }

        subTasks.put(subTask.getId(), subTask);

        epic.addSubTask(subTask.getId());

        // Обновление статуса связанного эпика
        updateEpicData(subTask.getEpicId());
        updateTaskInSortedSet(subTask);
    }

    /**
     * Добавление новой задачи
     *
     * @param task объект класса задачи
     */
    @Override
    public void addTask(Task task) {

        if (task == null) {
            return;
        }

        // Проверка на пересечение с другими задачами
        try {
            checkTaskOverlapToExist(task);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        // Назначение идентификатора
        if (task.getId() == null) {
            task.setId(idCounter);
            idCounter++;
        } else {
            if (tasks.containsKey(task.getId())) {
                System.out.println("Ошибка добавления новой задачи! Задача с таким идентификатором уже существует!");
            }
        }

        tasks.put(task.getId(), task);
        this.updateTaskInSortedSet(task);
    }

    /**
     * Редактирование эпика (реализуется путем передачи нового объекта)
     *
     * @param epic объект эпика
     */
    @Override
    public void editEpic(Epic epic) {
        this.epics.put(epic.getId(), epic);
    }

    /**
     * Редактирование подзадачи (реализуется путем передачи нового объекта)
     *
     * @param subTask объект подзадачи
     */
    @Override
    public void editSubTask(SubTask subTask) {

        // Проверка на пересечение с другими задачами по времени
        try {
            checkTaskOverlapToExist(subTask);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        // Удаление подзадачи из отсортированного множества
        SubTask oldSubTask = getSubTaskById(subTask.getId());
        if (oldSubTask != null) {
            prioritizedTasks.remove(oldSubTask);
        }

        Integer subTaskId = subTask.getId();
        subTasks.put(subTaskId, subTask);

        // Проверка, что у подзадачи изменился связанный с ней эпик
        Epic epic = epics.get(subTask.getEpicId());
        // Если связанный эпик не содержит в своем списке идентификаторов подзадач данной подзадачи,
        if (!epic.getSubTasksIds().contains(subTaskId)) {
            // Находим, к какому эпику принадлежала ранее данная подзадача и удаляем ее из коллекции
            for (Epic ep : epics.values()) {
                if (ep.getSubTasksIds().contains(subTaskId)) {
                    ep.getSubTasksIds().remove(subTaskId);
                    this.updateEpicData(ep.getId());
                }
            }
            epic.addSubTask(subTaskId);
        }

        this.updateTaskInSortedSet(subTask);
        this.updateEpicData(subTask.getEpicId());
    }

    /**
     * Редактирование задачи (реализуется путем передачи нового объекта)
     *
     * @param task объект задачи
     */
    @Override
    public void editTask(Task task) {

        // Проверка на пересечение с другими задачами
        try {
            checkTaskOverlapToExist(task);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        // Удаление задачи из отсортированного множества
        Task oldTask = getTaskById(task.getId());
        if (oldTask != null) {
            prioritizedTasks.remove(oldTask);
        }

        tasks.put(task.getId(), task);
        this.updateTaskInSortedSet(task);
    }

    /**
     * Удаление эпика по идентификатору
     *
     * @param id идентификатор эпика
     */
    @Override
    public void deleteEpic(int id) {

        Epic epic = this.epics.get(id);
        if (epic == null) {
            return;
        }

        // Удаление входящих в эпик подзадач
        epic.getSubTasksIds().stream().forEach(subTaskId -> {
            prioritizedTasks.remove(getSubTaskById(subTaskId));
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        });

        // Удаление переданного эпика
        epics.remove(id);
        prioritizedTasks.remove(epic);
        historyManager.remove(id);
    }

    /**
     * Удаление подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     */
    @Override
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
        this.updateEpicData(subTask.getEpicId());

        // Удаление подзадачи из общей коллекции
        subTasks.remove(id);
        prioritizedTasks.remove(subTask);
        historyManager.remove(id);
    }

    /**
     * Удалить задачу по переданному идентификатору
     *
     * @param id идентификатор задачи
     */
    @Override
    public void deleteTask(int id) {
        prioritizedTasks.remove(getTaskById(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    /**
     * Получить список подзадач эпика
     *
     * @param epicId идентификатор эпика
     * @return список подзадач
     */
    @Override
    public ArrayList<SubTask> getListEpicSubTasks(int epicId) {
        Epic epic = this.epics.get(epicId);
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();

        if (epic == null) {
            return null;
        }

        epic.getSubTasksIds().stream().forEach(subTaskId -> epicSubTasks.add(subTasks.get(subTaskId)));
        return epicSubTasks;
    }

    /**
     * Обновление данных эпика на основе данных подзадач, входящих в него: статуса и времен
     */
    private void updateEpicData(Integer epicId) {

        this.updateEpicStatus(epicId);
        this.updateEpicTimes(epicId);
    }

    /**
     * Получить историю просмотров (последние 10 задач)
     *
     * @return список с последними просмотренными задачами
     */
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
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
            if (subTasks.get(subTaskId).getStatus() != TaskStatus.DONE) {
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

    private void updateEpicTimes(int epicId) {

        Epic epic = epics.get(epicId);

        if (epic == null) {
            return;
        }

        TreeSet<Task> subTasks = new TreeSet<>(taskComparatorByDate);

        getListEpicSubTasks(epicId).forEach(subTask -> {
            if (subTask.getStartTime().isPresent())
                subTasks.add(subTask);
        });

        if (subTasks.size() > 0) {
            epic.setStartTime(subTasks.first().getStartTime().get());
            epic.setEndTime(subTasks.last().getEndTime());

            Duration duration = Duration.ZERO;
            for (Task subTask: subTasks) {
                duration = duration.plus(subTask.getDuration());
            }
            // Как это можно переписать через лямбду, с учетом что переменная должна быть final?
            // И в целом, приоритетнее ли в подобном случае использовать именно лямбду?
            // subTasks.forEach(subTask -> duration = duration.plus(subTask.getDuration()));

            epic.setDuration(duration);
        }
        else {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
        }

    }

    /**
     * Проверка двух задач на пересечение по датам
     *
     * @param task1
     * @param task2
     * @return
     */
    private boolean checkTasksOverlap(Task task1, Task task2) {

        if (task1.getId() != null && task2.getId() != null && task1.getId().equals(task2.getId())) {
            return false;
        }

        if (!task1.getStartTime().isPresent() || !task2.getStartTime().isPresent())
            return false;

        if ((task1.getStartTime().get().isAfter(task2.getStartTime().get()) &&
                task1.getStartTime().get().isBefore(task2.getEndTime())) ||

                (task1.getEndTime().isAfter(task2.getStartTime().get()) &&
                        task1.getEndTime().isBefore(task2.getEndTime()))) {
            return true;
        }

        return false;
    }

    /**
     * Проверка на пересечение задачи с имеющимися в списке
     *
     * @param task
     */
    private void checkTaskOverlapToExist(Task task) {
        if (prioritizedTasks.stream().filter(t -> checkTasksOverlap(task, t)).findAny().isPresent()) {
            String type = "задачи";
            if (task instanceof SubTask) {
                type = "подзадачи";
            }

            throw new RuntimeException("Ошибка! Указанная дата " + type + " пересекается с имеющейся задачей.");
        }
    }

}
