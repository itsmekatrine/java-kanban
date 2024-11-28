package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private TaskManager manager;
    private HistoryManager historyManager = new InMemoryHistoryManager();

    @BeforeEach
    public void setup() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addNewTask() {
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description");
        final int taskId = manager.createTask(task);
        historyManager.updateHistory(task);

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void add() {
        Task task = new Task(1,"Test addTask", "Test addTask description");
        historyManager.updateHistory(task);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldEqualityTasksById() {
        Task task1 = new Task(1,"Test Task 1", "Test description 1");
        Task task2 = new Task(1,"Test Task 2", "Test description 2");
        assertTrue(task1.equals(task2));

        Task task3 = new Task(2,"Test Task 3", "Test description 3");
        assertFalse(task1.equals(task3));
    }

    @Test
    void shouldChangeTitleInTask() {
        Task task = new Task(1,"Test Task 1", "Test description 1");
        manager.createTask(task);

        Task updateTask = new Task(task.getId(), "Test Task 2", task.getDescription());
        manager.updateTask(updateTask.getId(), task);

        Task requestedTask = manager.getTaskById(1);
        assertEquals("Test Task 1", requestedTask.getTitle());
    }

    @Test
    void shouldChangeDescriptionInTask() {
        Task task = new Task(1,"Test Task 1", "Test description 1");
        manager.createTask(task);

        Task updateTask = new Task(task.getId(), task.getTitle(), "Test description 2");
        manager.updateTask(updateTask.getId(), task);

        Task requestedTask = manager.getTaskById(1);
        assertEquals("Test description 1", requestedTask.getDescription());
    }
}