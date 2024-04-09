package ru.nikishechkin.kanban.model;

import java.util.HashSet;

public class Epic extends Task {

    private HashSet<Integer> subTasksIds;

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
        this.subTasksIds = new HashSet<>();
    }

    public HashSet<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void addSubTask(Integer subTaskId) {
        if (subTaskId != null && subTaskId != this.id) {
            this.subTasksIds.add(subTaskId);
        }
    }
}
