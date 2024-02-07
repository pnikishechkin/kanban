import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> listTasks;

    public Epic(String name, String description) {
        super(name, description);
        this.listTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getListTasks() {
        return listTasks;
    }

    public void addTask(SubTask task) {
        if (task != null) {
            this.listTasks.add(task);
        }
    }
}
