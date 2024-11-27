package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;

    @BeforeEach
    void setup() {
    manager = new InMemoryTaskManager();
    }

    @Test
    void addNewDifferenceTasks() {
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description");
        manager.createTask(task);

        List<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.contains(task));
        assertEquals(1, tasks.size());
    }

    @Test
    void addNewDifferenceEpics() {
        Epic epic = new Epic(1,"Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);

        List<Epic> tasks = manager.getAllEpics();
        assertTrue(tasks.contains(epic));
        assertEquals(1, tasks.size());
    }

    @Test
    void addNewDifferenceSubtasks() {
        Epic epic = new Epic(1,"Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask(2,"Test Subtask", "Test description", epic.getId());

        manager.createSubtask(epic.getId(), subtask);
        manager.createEpic(epic);

        List<Subtask> tasks = manager.getAllSubtasks();

        assertTrue(tasks.contains(subtask));
        assertEquals(1, tasks.size());
    }

    @Test
    void shouldGetTaskById() {
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description");
        manager.createTask(task);

        Task foundTask = manager.getTaskById(task.getId());
        assertEquals(task, foundTask);
    }

    @Test
    void shouldGetEpicById() {
        Epic epic = new Epic(1,"Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);

        Epic foundEpic = manager.getEpicById(epic.getId());
        assertEquals(epic, foundEpic);
    }

    @Test
    void shouldGetSubtaskById() {
        Epic epic = new Epic(1,"Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask(2,"Test Subtask", "Test description", epic.getId());
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        Subtask foundSubtask = manager.getSubtaskById(subtask.getId());
        assertEquals(subtask, foundSubtask);
    }

    @Test
    void shouldGetSubtasksByEpic() {
        Epic epic = new Epic(1,"Test addNewEpic 1", "Test addNewEpic description 1");
        Subtask subtask1 = new Subtask(2, "Test Subtask 1", "Test description 1",epic.getId());
        Subtask subtask2 = new Subtask(3, "Test Subtask 2", "Test description 2",epic.getId());

        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask1);
        manager.createSubtask(epic.getId(), subtask2);

        List<Subtask> subtasks = manager.getAllSubtasksOfEpic(epic.getId());
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtask1.getId() == subtask1.getId());
        assertTrue(subtasks.contains(subtask2));
        assertTrue(subtask2.getId() == subtask2.getId());
    }

    @Test
    void shouldRemoveTaskById() {
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description");
        manager.createTask(task);

        boolean removed = manager.deleteTaskById(task.getId());
        assertTrue(removed);

        boolean removedTask = manager.deleteTaskById(task.getId());
        assertTrue(removedTask);
    }

    @Test
    void shouldRemoveEpicById() {
        Epic epic = new Epic(1,"Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);

        boolean removed = manager.deleteEpicById(epic.getId());
        assertTrue(removed);

        boolean removedEpic = manager.deleteEpicById(epic.getId());
        assertTrue(removedEpic);
    }

    @Test
    void shouldRemoveSubtaskById() {
        Epic epic = new Epic(1,"Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask(2, "Test Subtask", "Test description", epic.getId());
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        boolean removed = manager.deleteSubtaskById(subtask.getId());
        assertTrue(removed);

        boolean removedSubtask = manager.deleteSubtaskById(subtask.getId());
        assertTrue(removedSubtask);
    }

    @Test
    void shouldAddTaskWithUniqueId() {
        Task task1 = new Task(1,"Test addNewTask", "Test addNewTask description");
        Task task2 = new Task(2,"Test addNewTask", "Test addNewTask description");

        assertDoesNotThrow(() -> manager.createTask(task1));
        assertDoesNotThrow(() -> manager.createTask(task2));

        assertEquals(task1, manager.getTaskById(1));
        assertEquals(task2, manager.getTaskById(2));
    }

    @Test
    void shouldAddTasksWithDifferentsId() {
        Task task1 = new Task(1,"Test addNewTask", "Test addNewTask description");
        Task task2 = new Task(2,"Test addNewTask", "Test addNewTask description");

        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void shouldEqualsIdOfSameTasks() {
        Task task1 = new Task(1,"Test addNewTask1", "Test addNewTask description1");
        Task task2 = new Task(1,"Test addNewTask2", "Test addNewTask description2");

        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void shouldReturnNullIfNotExistTask() {
        Task task1 = new Task(1,"Test addNewTask", "Test addNewTask description");
        manager.createTask(task1);

        assertNull(manager.getTaskById(2));
    }

    @Test
    void shouldAddEpicAndSubtaskWithDifferentId() {
        Epic epic1 = new Epic(1,"Test addNewEpic1", "Test addNewEpic description1");
        Epic epic2 = new Epic(1,"Test addNewEpic2", "Test addNewEpic description2");

        Subtask subtask1 = new Subtask(2, "Test Subtask1", "Test description1",1);
        Subtask subtask2 = new Subtask(3, "Test Subtask2", "Test description2",1);

        assertNotEquals(epic1.hashCode(), epic2.hashCode());
        assertNotEquals(subtask1.hashCode(), subtask2.hashCode());
    }
}