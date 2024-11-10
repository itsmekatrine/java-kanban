package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.List;

public interface TaskManager {
    // методы для задач
    List<Task> getAllTasks();

    int createTask(Task task);

    Task getTaskById(int id);

    void updateTask(int id, Task task);

    void deleteAllTasks();

    // методы для подзадач
    List<Subtask> getAllSubtasks();

    int createSubtask(int epicId, Subtask subtask);

    Subtask getSubtaskById(int id);

    List<Subtask> getAllSubtasksOfEpic (int id);

    void updateSubtask(int id, Subtask newSubtask);

    void deleteAllSubtasks();

    // методы для эпиков
    List<Epic> getAllEpics();

    int createEpic(Epic epic);

    Epic getEpicById(int id);

    void updateEpic(int id, Epic epic);

    void deleteEpicById(int id);

    void deleteAllEpics();

    // метод для просмотра последних задач
    List<Task> getHistory();
}
