package com.yandex.app.model;

import com.yandex.app.service.TaskType;

public class Subtask extends Task {
    private int epicId;
    private final TaskType type;

    public Subtask(int id, String title, String description, int epicId) {
        super(id, title, description);
        if (id == epicId) {
            throw new IllegalArgumentException(
                    String.format("Эпик с идентификатором %d не может быть подзадачей самого себя (подзадача: %d)", epicId, id));
        }
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public TaskType getType() {
        return type;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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