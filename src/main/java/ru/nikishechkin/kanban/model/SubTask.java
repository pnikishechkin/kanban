package ru.nikishechkin.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private Integer epicId;

    public SubTask(Integer id, String name, String description, TaskStatus taskStatus,
                   LocalDateTime startTime, Duration duration, Integer epicId) {
        super(id, name, description, taskStatus, startTime, duration);
        this.setEpicId(epicId);
        this.type = TaskType.SUBTASK;
    }

    public SubTask(String name, String description, LocalDateTime startTime, Duration duration, Integer epicId) {
        super(name, description, startTime, duration);
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
