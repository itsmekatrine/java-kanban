package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private HistoryManager history;
    protected TreeSet<Task> prioritizedTasks;

    private int currentTaskId = 0;
    private int currentSubtaskId = 100;
    private int currentEpicId = 1000;

    public InMemoryTaskManager(HistoryManager history) {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.history = history;
        prioritizedTasks = new TreeSet<>(
                Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Task::getId)
        );
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        if (hasCrossingTasks(task)) {
            throw new IllegalArgumentException("Есть пересечение с другой задачей");
        }
        int id = ++currentTaskId;
        tasks.put(id, task);
        history.updateHistory(task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        save();
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
        Task oldTask = getTasks().get(id);
        if (oldTask == null) {
            throw new IllegalArgumentException("Задача с указанным id не существует");
        }
        prioritizedTasks.remove(oldTask);
        if (hasCrossingTasks(task)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается с другой задачей");
        }
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        tasks.put(id, task);
        save();
    }

    @Override
    public boolean deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            history.remove(id);
            save();
            return true;
        }
        return false;
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
        if (hasCrossingTasks(subtask)) {
            throw new IllegalArgumentException("Есть пересечение с другой подзадачей");
        }
        int id = ++currentSubtaskId;
        subtasks.put(id, subtask);
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            epic.getSubtasks().add(subtask);
            epic.updateEpicStatus();
            epic.calculateEpicDuration();
            history.updateHistory(subtask);
        }
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        save();
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
        Subtask oldSubtask = getAllSubtasks().get(id);
        if (oldSubtask == null) {
            throw new IllegalArgumentException("Подзадача с указанным id не существует");
        }
        prioritizedTasks.remove(oldSubtask);
        if (hasCrossingTasks(newSubtask)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается с другой подзадачей");
        }
        if (newSubtask.getStartTime() != null) {
            prioritizedTasks.add(newSubtask);
        }
        subtasks.put(id, newSubtask);
        Integer epicId = newSubtask.getEpicId();
        if (epicId != null) {
            Epic epic = getEpicById(id);
            if (epic != null) {
                epic.updateEpicStatus();
            }
        }
        save();
    }

    @Override
    public boolean deleteSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask == null) {
            return false;
        }
        boolean isRemoved = subtasks.remove(id) != null;
        history.remove(id);
        prioritizedTasks.remove(subtask);

        Epic epic = getEpicById(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtaskIds().removeIf(taskId -> taskId == id);
            epic.updateEpicStatus();
        }
        save();
        return isRemoved;
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
        save();
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
        save();
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
            prioritizedTasks.remove(subtask);
        });

        prioritizedTasks.remove(epic);
        boolean isRemoved = epics.remove(id) != null;
        history.remove(id);

        save();
        return isRemoved;
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(this::deleteEpicById);
    }

    protected boolean hasCrossingTasks(Task task) {
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isCrossTasks(existingTask, task));
    }

    public boolean isCrossTasks(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    public void checkCrossTasks() {
        List<Task> tasks = getPrioritizedTasks();
        for (int i = 0; i < tasks.size() - 1; i++) {
            Task currentTask = tasks.get(i);
            Task nextTask = tasks.get(i + 1);

            if (currentTask.getEndTime().isAfter(nextTask.getStartTime())) {
                System.out.println("Задачи " + currentTask + " и " + nextTask + " пересекаются");
            }
        }
    }

    protected void save() {
    }
}
