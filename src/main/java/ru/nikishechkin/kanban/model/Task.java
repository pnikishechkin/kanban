package ru.nikishechkin.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task implements Comparable<Task> {

    protected Integer id;
    protected String name;
    protected TaskStatus status;
    protected String description;
    protected TaskType type;
    protected Duration duration = Duration.ZERO;
    protected Optional<LocalDateTime> startTime;

    public Task(Integer id, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this(name, description, startTime, duration);
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = TaskType.TASK;
        this.startTime = Optional.ofNullable(startTime);

        this.duration = duration;
        if (duration == null)
            this.duration = Duration.ZERO;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.status = taskStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = Optional.ofNullable(startTime);
    }

    public TaskType getType() {
        return type;
    }

    public LocalDateTime getEndTime() {
        return startTime.isPresent() ? startTime.get().plus(duration) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + "," + type.toString() + "," + name + "," + status.toString() + "," + description + "," +
                startTime.get() + "," + duration.toMinutes();
    }

    @Override
    public int compareTo(Task o) {
        if (o.startTime.isPresent() && this.startTime.isPresent()) {
            return this.startTime.get().compareTo(o.startTime.get());
        }
        return 1;
    }
}
