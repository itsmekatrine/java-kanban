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

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        history = Managers.getDefaultHistory();
    }

    // методы для задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskByTitle(String title) {
        for (Task t : tasks.values()) {
            if (t.getTitle().equals(title)) {
                return t;
            }
        }
        return null;
    }

    public Task getTaskByDescription(String description) {
        for (Task t : tasks.values()) {
            if (t.getDescription().equals(description)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public int createTask(Task task) {
        int id = ++currentTaskId;
        tasks.put(id, task);
        if (history != null) {
            history.updateHistory(task);
        }
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
        Task task = getTaskById(id);
        if (task == null) {
            return false;
        } else {
            boolean isRemoved = tasks.remove(task) == null;
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
        Subtask subtask = getSubtaskById(id);
        if (subtask == null) {
            return false;
        } else {
            boolean isRemoved = subtasks.remove(subtask) == null;
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
        int id = ++currentEpicId;
        epics.put(id, epic);
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
        if (epic != null) {
            List<Subtask> subtasksOfEpic = epic.getSubtasks();
            for (Subtask subtask : subtasksOfEpic) {
                deleteSubtaskFromEpic(id);
            }
            boolean isRemoved = epics.remove(epic) == null;
            history.remove(id);
            if (isRemoved) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void deleteAllEpics() {
        List<Integer> idOfEpics = new ArrayList<>(epics.keySet());

        for (int id : idOfEpics) {
            deleteEpicById(id);
        }
    }
}
