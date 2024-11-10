package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private int currentId = 0;
    private ArrayList<Task> history;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        history = new ArrayList<>(10);
    }

    // методы для задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public int createTask(Task task) {
        int id = ++currentId;
        tasks.put(id, task);
        return id;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            updateHistory(task);
        }
        return task;
    }

    @Override
    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            tasks.replace(id, task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        List<Integer> idOfTasks = new ArrayList<>(tasks.keySet());

        for (int id : idOfTasks) {
            deleteTaskById(id);
        }
    }

    // методы для подзадач
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Subtask> getAllSubtasksOfEpic (int id) {
        Epic epic = epics.get(id);
        return new ArrayList<>(epic.getSubtasks());
    }

    @Override
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

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            updateHistory(subtask);
        }
        return subtask;
    }

    @Override
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

    public void deleteSubtaskFromEpic(int id) {
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

    @Override
    public void deleteAllSubtasks() {
        List<Integer> idOfSubtasks = new ArrayList<>(subtasks.keySet());

        for (int id : idOfSubtasks) {
            deleteSubtaskFromEpic(id);
        }
    }

    // методы для эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public int createEpic(Epic epic) {
        int id = ++currentId;
        epics.put(id, epic);
        return id;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            updateHistory(epic);
        }
        return epic;
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            epics.replace(id, epic);
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            List<Subtask> subtasksOfEpic = epic.getSubtasks();
            for (Subtask subtask : subtasksOfEpic) {
                deleteSubtaskFromEpic(id);
            }
        }
        epics.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        List<Integer> idOfEpics = new ArrayList<>(epics.keySet());

        for (int id : idOfEpics) {
            deleteEpicById(id);
        }
    }

    // метод для просмотра последних задач
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    public void updateHistory(Task task) {
        if (!history.contains(task)) {
            if (history.size() == 10) {
                history.removeFirst();
            }
            history.addLast(task);
        }
    }
}
