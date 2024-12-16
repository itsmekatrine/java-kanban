package com.yandex.app.model;

import com.yandex.app.service.StatusTask;
import com.yandex.app.service.TaskType;

import java.util.Objects;

public class Task {
    private int id;
    private final String title;
    private final String description;
    private StatusTask status;
    private final TaskType type;

    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = StatusTask.NEW;
        this.type = TaskType.TASK;
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

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public int getId() {
        return id;
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
        return String.format("%d,%s,%s,%s,%s",
                id, getType(), title, status, description);
    }
}
