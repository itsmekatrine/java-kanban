import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public int createSubtask(Subtask subtask) {
        int id = ++currentId;
        subtasks.put(id, subtask);
        return id;
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void updateSubtask(int id, Subtask subtask) {
        if (subtasks.containsKey(id)) {
            subtasks.replace(id, subtask);
        }
    }

    public void deleteSubtaskById(int id) {
        subtasks.remove(id);
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