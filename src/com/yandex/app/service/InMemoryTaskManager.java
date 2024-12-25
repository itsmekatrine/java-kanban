package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private int currentTaskId = 0;
    private int currentSubtaskId = 100;
    private int currentEpicId = 1000;
    private HistoryManager history;

    public InMemoryTaskManager(HistoryManager history) {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.history = history;
    }

    // методы для задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Task getTaskByTitle(String title) {
        return tasks.values().stream()
                .filter(t -> t.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Task getTaskByDescription(String description) {
        return tasks.values().stream()
                .filter(t -> t.getDescription().equals(description))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int createTask(Task task) {
        int id = ++currentTaskId;
        tasks.put(id, task);
        history.updateHistory(task);
        return id;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null && history != null) {
            history.updateHistory(task);
        }
        return task;
    }

    @Override
    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            tasks.replace(id, task);
        }
    }

    @Override
    public boolean deleteTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return false;
        } else {
            boolean isRemoved = tasks.remove(id) != null;
            history.remove(id);
            if (isRemoved) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(this::deleteTaskById);
    }

    // методы для подзадач
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksOfEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            return epic.getSubtasks();
        }
        return Collections.emptyList();
    }

    @Override
    public int createSubtask(int epicId, Subtask subtask) {
        int id = ++currentSubtaskId;
        subtasks.put(id, subtask);
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            epic.getSubtasks().add(subtask);
            epic.updateEpicStatus();
            epic.calculateEpicDuration();
            history.updateHistory(subtask);
        }
        return id;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            if (history != null) {
                history.updateHistory(subtask);
            }
            return subtask;
        }
        return null;
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

    @Override
    public boolean deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return false;
        } else {
            boolean isRemoved = subtasks.remove(id) != null;
            history.remove(id);
            Epic epic = getEpicById(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().removeIf(taskId -> taskId == id);
                epic.updateEpicStatus();
            }
            if (isRemoved) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void deleteSubtaskFromEpic(int id) {
        if (subtasks.containsKey(id)) {
            Subtask removeSubtask = subtasks.remove(id);
            history.remove(id);
            getAllEpics().stream()
                    .filter(epic -> epic.hasSubtask(epic, removeSubtask))
                    .forEach(epic -> {
                        epic.removeSubtask(removeSubtask);
                        epic.updateEpicStatus();
                    });
        }
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(this::deleteSubtaskFromEpic);
    }

    // методы для эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public int createEpic(Epic epic) {
        int id = ++currentEpicId;
        epics.put(id, epic);
        history.updateHistory(epic);
        return id;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            if (history != null) {
                history.updateHistory(epic);
            }
            return epic;
        }
        return null;
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            epics.replace(id, epic);
        }
    }

    @Override
    public boolean deleteEpicById(int id) {
        Epic epic = getEpicById(id);
        if (epic == null) {
            return false;
        }

        List<Subtask> subtasksOfEpic = new ArrayList<>(epic.getSubtasks());
        subtasksOfEpic.forEach(subtask -> {
            deleteSubtaskFromEpic(subtask.getId());
            history.remove(subtask.getId());
        });

        boolean isRemoved = epics.remove(id) != null;
        history.remove(id);
        return isRemoved;
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(this::deleteEpicById);
    }
}
