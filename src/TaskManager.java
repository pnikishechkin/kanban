import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> taskHashMap;

    public Collection<Task> getTaskHashMap() {
        return taskHashMap.values();
    }

    public void clearListTasks() {
        this.taskHashMap.clear();
    }

    public Task getTaskById(int id) {
        return this.taskHashMap.get(id);
    }

    public void addTask(Task task) {
        if (task != null) {
            this.taskHashMap.put(task.getId(), task);
        }
    }

    public void editTask(Task editedTask) {
        // this.listTasks.replaceAll();
        this.taskHashMap.replace(editedTask.getId(), editedTask);
    }

    public void deleteTask(int id) {
        // Удаление всех подзадач, если удаляемая задача является эпиком
        Task task = this.taskHashMap.get(id);
        if (task instanceof Epic) {
            for (SubTask subTask : ((Epic) task).getListTasks()) {
                this.deleteTask(subTask.getId());
            }
        }

        // Удаление переданной задачи
        this.taskHashMap.remove(id);
    }

    /**
     * Получить список подзадач эпика
     * @param epicId
     * @return
     */
    public ArrayList<SubTask> getListSubTasks(int epicId) {
        Task epic = this.taskHashMap.get(epicId);
        if (epic instanceof Epic) {
            return ((Epic) epic).getListTasks();
        } else {
            return null;
        }
    }
}
