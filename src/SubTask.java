public class SubTask extends Task {

    private Epic epic;

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;

        if (!this.epic.getListTasks().contains(epic)) {
            this.epic.addTask(this);
        }
    }

}
