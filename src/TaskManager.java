import java.util.ArrayList;

public class TaskManager {
    private ArrayList<Task> listTasks;

    public ArrayList<Task> getListTasks() {
        return listTasks;
    }

    public void clearListTasks() {
        this.listTasks.clear();
    }

    public Task getTaskById(int id) {
        return this.listTasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
    }

    public void addTask(Task task) {
        if (task != null) {
            this.listTasks.add(task);
        }
    }

    public void editTask(Task editedTask) {
        // this.listTasks.replaceAll();
        for (Task task : listTasks) {
            if (task.getId() == editedTask.getId()) {
                task = editedTask;
            }
        }
    }

    public void deleteTask(int id) {
        this.listTasks.removeIf(task -> task.getId() == id);
    }

    public
}
