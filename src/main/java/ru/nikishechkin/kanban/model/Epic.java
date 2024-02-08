package ru.nikishechkin.kanban.model;

import java.util.HashSet;

public class Epic extends Task {

    private HashSet<Integer> subTasksIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasksIds = new HashSet<>();
    }

    public HashSet<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void addSubTask(Integer subTaskId) {
        if (subTaskId != null) {
            this.subTasksIds.add(subTaskId);
        }
    }
}
