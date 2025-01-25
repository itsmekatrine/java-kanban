package com.yandex.app.model;

import com.yandex.app.service.StatusTask;
import com.yandex.app.service.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(int id, String title, String description) {
        super(id, title, description, Duration.ZERO, null);
        this.subtasks = new ArrayList<>();
        this.endTime = null;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public List<Integer> getSubtaskIds() {
        List<Integer> ids = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            ids.add(subtask.getId());
        }
        return ids;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask removeSubtask) {
        subtasks.remove(removeSubtask);
    }

    public boolean hasSubtask(Subtask subtask) {
        return subtasks.contains(subtask);
    }

    // зависимость статуса эпика от подзадач
    public void updateEpicStatus() {
        boolean allDone = true;
        boolean hasInProgress = false;
        boolean hasNew = false;
        boolean hasDone = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == StatusTask.IN_PROGRESS) {
                hasInProgress = true;
            }
            if (subtask.getStatus() == StatusTask.NEW) {
                hasNew = true;
            }
            if (subtask.getStatus() == StatusTask.DONE) {
                hasDone = true;
            }
            if (subtask.getStatus() != StatusTask.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            this.setStatus(StatusTask.DONE);
        } else if (hasInProgress || (hasDone && hasNew)) {
            this.setStatus(StatusTask.IN_PROGRESS);
        } else {
            this.setStatus(StatusTask.NEW);
        }
    }

    // подсчёт продолжительности выполнения эпика
    public Duration calculateDurationAndStartEndTime() {
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.MAX;
        this.endTime = LocalDateTime.MIN;

        this.duration = subtasks.stream()
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = subtasks.stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        this.endTime = subtasks.stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return this.duration;
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
        return super.toString();
    }
}