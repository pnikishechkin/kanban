package ru.nikishechkin.kanban.model;

public class SubTask extends Task {

    private Integer idEpic;

    public SubTask(Integer id, String name, String description, TaskStatus taskStatus, Integer idEpic) {
        super(id, name, description, taskStatus);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, Integer idEpic) {
        super(name, description);
        this.idEpic = idEpic;
    }

    public Integer getEpicId() {
        return idEpic;
    }
}
