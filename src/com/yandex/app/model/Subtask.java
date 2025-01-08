package com.yandex.app.model;

import com.yandex.app.service.StatusTask;
import com.yandex.app.service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;
    private Duration duration;
    private LocalDateTime startTime;

    public Subtask(int id, String title, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, title, description,duration, startTime);
        if (id == epicId) {
            throw new IllegalArgumentException(
                    String.format("Эпик с идентификатором %d не может быть подзадачей самого себя (подзадача: %d)", epicId, id));
        }
        this.epicId = epicId;
        this.duration = duration;
        this.startTime = startTime != null ? startTime : LocalDateTime.now();
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public void setStatus(StatusTask status, Epic epic) {
        super.setStatus(status);
        if (epic != null) {
            epic.updateEpicStatus();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + epicId;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s,%d", super.toString(), epicId);
    }
}