package com.yandex.app.model;

import com.yandex.app.service.StatusTask;
import com.yandex.app.service.TaskType;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private final String title;
    private final String description;
    private StatusTask status;
    protected TaskType type;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(int id, String title, String description, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = StatusTask.NEW;
        this.type = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime != null ? startTime : LocalDateTime.now();
    }

    public TaskType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setTaskType(TaskType type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип задачи не может быть null");
        }
        this.type = type;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd–MM–yy | HH:mm");
        String formattedStartTime = (startTime != null) ? startTime.format(formatter) : "null";
        return String.format("%d,%s,%s,%s,%s,%d,%s",
                id, getType(), title, status, description, duration.toMinutes(), formattedStartTime);
    }
}
