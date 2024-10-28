package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.*;

public class TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private int currentId = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    // методы для задач
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public int createTask(Task task) {
        int id = ++currentId;
        tasks.put(id, task);
        return id;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            tasks.replace(id, task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // методы для подзадач
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public int createSubtask(int epicId, Subtask subtask) {
        int id = ++currentId;
        subtasks.put(id, subtask);
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            epic.getSubtasks().add(subtask);
            epic.updateEpicStatus();
        }
        return id;
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void updateSubtask(int id, Subtask newSubtask) {
        if (subtasks.containsKey(id)) {
            Subtask oldSubtask = subtasks.get(id);
            subtasks.replace(id, newSubtask);

            Integer epicId = oldSubtask.getEpicId();
            if (epicId != null) {
                Epic epic = getEpicById(id);
                if (epic != null) {
                    epic.updateEpicStatus();
                }
            }
        }
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask removeSubtask = subtasks.remove(id);

            List<Epic> allEpics = getAllEpics();
            for (Epic epic : allEpics) {
                if (epic.hasSubtask(epic, removeSubtask)) {
                    epic.removeSubtask(removeSubtask);
                    epic.updateEpicStatus();
                }
            }
        }
    }

    public void deleteAllSubtasks() {
        List<Integer> idOfSubtasks = new ArrayList<>(subtasks.keySet());

        for (int id : idOfSubtasks) {
            deleteSubtaskById(id);
        }
    }

    // методы для эпиков
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public int createEpic(Epic epic) {
        int id = ++currentId;
        epics.put(id, epic);
        return id;
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            epics.replace(id, epic);
        }
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }
}