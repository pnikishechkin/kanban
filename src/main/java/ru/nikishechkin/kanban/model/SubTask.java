package ru.nikishechkin.kanban.model;

public class SubTask extends Task {

    private Integer epicId;

    public SubTask(Integer id, String name, String description, TaskStatus taskStatus, Integer epicId) {
        super(id, name, description, taskStatus);
        this.setEpicId(epicId);
        this.type = TaskType.SUBTASK;
    }

    public SubTask(String name, String description, Integer epicId) {
        super(name, description);
        this.setEpicId(epicId);
        this.type = TaskType.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        if (epicId != this.id) {
            this.epicId = epicId;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "," + epicId;
    }
}
