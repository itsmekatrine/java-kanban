package com.yandex.app.service;

import com.yandex.app.exception.NotFoundException;
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

    public List<Task> getHistory() {
        return history.getHistory();
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
        task.setId(id);
        task.setStatus(StatusTask.NEW);
        task.setTaskType(TaskType.TASK);
        tasks.put(id, task);
        System.out.println("Task stored successfully: " + task);
        history.updateHistory(task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
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
        Task existingTask = tasks.get(id);
        if (existingTask == null) {
            throw new NotFoundException("Задача с указанным id не существует: " + id);
        }
        if (existingTask.getStartTime() != null) {
            prioritizedTasks.remove(existingTask);
        }
        if (hasCrossingTasks(task)) {
            throw new IllegalArgumentException("Есть пересечение с другой задачей");
        }
        task.setId(id);
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public boolean deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            history.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllTasks() {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        List<Integer> taskIds = new ArrayList<>(tasks.keySet());
        taskIds.forEach(this::deleteTaskById);
        tasks = new HashMap<>();
        prioritizedTasks.removeIf(task -> task instanceof Task);
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
        Epic epic = getEpicById(epicId);
        if (epic.getSubtasks() == null) {
            throw new IllegalStateException("Список подзадач в эпике не инициализирован");
        }
        if (hasCrossingTasks(subtask)) {
            throw new IllegalArgumentException("Есть пересечение с другой подзадачей");
        }
        subtask.setId(++currentSubtaskId);
        subtasks.put(subtask.getId(), subtask);
        subtask.setStatus(StatusTask.NEW);
        subtask.setTaskType(TaskType.SUBTASK);
        subtask.setEpicId(epicId);

        epic.addSubtask(subtask);
        epic.updateEpicStatus();
        epic.calculateDurationAndStartEndTime();
        history.updateHistory(subtask);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        return subtask.getId();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с указанным id не существует: " + id);
        }

        if (history != null) {
            history.updateHistory(subtask);
        }
        return subtask;
    }

    @Override
    public void updateSubtask(int id, Subtask newSubtask) {
        Subtask oldSubtask = getAllSubtasks().get(id);
        if (oldSubtask == null) {
            throw new NotFoundException("Подзадача с указанным id не существует");
        }
        prioritizedTasks.remove(oldSubtask);
        if (hasCrossingTasks(newSubtask)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается с другой подзадачей");
        }
        if (newSubtask.getStartTime() != null) {
            prioritizedTasks.add(newSubtask);
        }
        newSubtask.setId(id);
        subtasks.put(id, newSubtask);
        Integer epicId = newSubtask.getEpicId();
        if (epicId != null) {
            Epic epic = getEpicById(id);
            if (epic != null) {
                epic.updateEpicStatus();
            }
        }
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
        return isRemoved;
    }

    @Override
    public void deleteSubtaskFromEpic(int id) {
        Subtask removeSubtask = subtasks.remove(id);
        if (removeSubtask == null) {
            throw new NotFoundException("Подзадача с указанным id не существует");
        }
        if (history != null) {
            history.remove(id);
        }
        getAllEpics().stream()
                .filter(epic -> epic.hasSubtask(removeSubtask))
                .forEach(epic -> {
                    epic.removeSubtask(removeSubtask);
                    epic.updateEpicStatus();
                    epic.calculateDurationAndStartEndTime();
                });
        prioritizedTasks.remove(removeSubtask);
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(this::deleteSubtaskFromEpic);
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
    }

    // методы для эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Эпик не может быть null");
        }
        int id = ++currentEpicId;
        epic.setId(id);
        epic.setStatus(StatusTask.NEW);
        epic.setTaskType(TaskType.EPIC);
        if (epic.getSubtasks() == null) {
            epic.setSubtasks(new ArrayList<>());
        }
        epic.calculateDurationAndStartEndTime();
        epics.put(id, epic);
        history.updateHistory(epic);
        return id;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с указанным id не существует: " + id);
        }
        if (history != null) {
            history.updateHistory(epic);
        }
        return epic;
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        Epic existingEpic = epics.get(id);
        if (existingEpic == null) {
            throw new NotFoundException("Эпик с указанным id не существует: " + id);
        }
        epic.setId(id);
        epics.put(id, epic);
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
        return isRemoved;
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(this::deleteEpicById);
        prioritizedTasks.removeIf(task -> task instanceof Epic || epics.values().stream()
                .anyMatch(epic -> epic.getSubtasks().contains(task)));
    }

    private boolean hasCrossingTasks(Task task) {
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isCrossTasks(existingTask, task));
    }

    private boolean isCrossTasks(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
