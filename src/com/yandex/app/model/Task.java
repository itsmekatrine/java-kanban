package com.yandex.app.model;

import com.yandex.app.service.StatusTask;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private StatusTask status;

    public Task (int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = StatusTask.NEW;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "com.yandex.app.model.Task{" +
                "id=" + id +
                "title=" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
