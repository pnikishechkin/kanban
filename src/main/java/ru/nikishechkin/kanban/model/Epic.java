package ru.nikishechkin.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;

public class Epic extends Task {

    private HashSet<Integer> subTasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, null, null);
        this.description = description;
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

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
