package com.yandex.app.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String description, int epicId) {
        if (id == epicId) {
            throw new IllegalArgumentException(
                    String.format("Эпик не может быть подзадачей самого себя ", epicId, id));
        }
        super(id, title, description);
        this.epicId = epicId;
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
        return "com.yandex.app.model.Subtask{" +
                "epicId=" + epicId +
                ", id=" + getId() +
                "title=" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}