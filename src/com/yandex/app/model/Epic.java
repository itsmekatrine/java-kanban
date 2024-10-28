package com.yandex.app.model;

import com.yandex.app.service.StatusTask;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
        this.status = StatusTask.NEW;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    // зависимость статуса эпика от подзадач
    public StatusTask updateEpicStatus() {
        boolean allDone = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != StatusTask.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            return StatusTask.DONE;
        } else {
            for (Subtask subtask : subtasks) {
                if (subtask.getStatus() == StatusTask.IN_PROGRESS) {
                    return StatusTask.IN_PROGRESS;
                }
            }
            return StatusTask.NEW;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Epic{" +
                "title=" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}