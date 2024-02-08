package ru.nikishechkin.kanban.model;

public class SubTask extends Task {

    private Integer epicId;

    public SubTask(Integer id, String name, String description, TaskStatus taskStatus, Integer epicId) {
        super(id, name, description, taskStatus);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }
}
