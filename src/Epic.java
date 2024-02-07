import java.util.ArrayList;

public class Epic extends Task {

    // Список подзадач, привязанных к данному эпику
    // Также как мысль - можно хранить не ссылки на объекты подзадач, а только их идентификаторы, будет проще
    // если добавляем новую подзадачу с идентификатором, который уже был ранее создан, т.е редактируем
    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getListSubTasks() {
        return subTasks;
    }

    public void addTask(SubTask task) {
        if (task != null) {
            this.subTasks.add(task);
        }
    }

    /**
     * Проверка статуса подзадач, входящих в эпик, и изменение статуса эпика при необходимости
     */
    public void checkStatus() {

        if (subTasks.size() == 0) {
            this.taskStatus = TaskStatus.NEW;
            return;
        }

        // Флаг, сигнализирующий о том, что все задачи эпика являются новыми
        boolean allNew = true;

        // Флаг, сигнализирующий о том, что все задачи эпика являются решенными
        boolean allDone = true;

        for (SubTask subTask: subTasks) {
            // Если хотя бы у одной задачи статус не Новой, снимаем флаг
            if(subTask.getTaskStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            // Если хотя бы у одной задачи статус не решенной, снимаем флаг
            if(subTask.getTaskStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            this.taskStatus = TaskStatus.NEW;
        } else if (allDone) {
            this.taskStatus = TaskStatus.DONE;
        } else {
            this.taskStatus = TaskStatus.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        String str = id + " | " + name + "\n" + "----------------------------------------" + "\n";

        for (SubTask subTask: subTasks) {
            str = str.concat(subTask.toString() + "\n");
        }
        str = str.concat("========================================");
        return str;
    }
}
