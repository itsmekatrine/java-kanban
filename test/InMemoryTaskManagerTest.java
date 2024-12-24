package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setup() {
    manager = Managers.getDefault();
    }

    @Test
    void addNewDifferenceTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime);
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
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1,"Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask(2,"Test Subtask", "Test description", epic.getId(), Duration.ofHours(2), startTime);

        manager.createSubtask(epic.getId(), subtask);
        manager.createEpic(epic);

        List<Subtask> tasks = manager.getAllSubtasks();

        assertTrue(tasks.contains(subtask));
        assertEquals(1, tasks.size());
    }

    @Test
    void shouldGetTaskById() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime);
        manager.createTask(task);

        Task foundTask = manager.getTaskById(task.getId());
        assertEquals(task, foundTask);
    }

    @Test
    void shouldGetEpicById() {
        Epic epic = new Epic(1001,"Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);

        Epic foundEpic = manager.getEpicById(epic.getId());
        assertEquals(epic, foundEpic);
    }

    @Test
    void shouldGetSubtaskById() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask(101,"Test Subtask", "Test description", epic.getId(), Duration.ofHours(2), startTime);
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        Subtask foundSubtask = manager.getSubtaskById(subtask.getId());
        assertEquals(subtask, foundSubtask);
    }

    @Test
    void shouldGetSubtasksByEpic() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Test addNewEpic 1", "Test addNewEpic description 1");
        Subtask subtask1 = new Subtask(101, "Test Subtask 1", "Test description 1",epic.getId(), Duration.ofHours(1), startTime);
        Subtask subtask2 = new Subtask(102, "Test Subtask 2", "Test description 2",epic.getId(), Duration.ofHours(2), startTime.plusHours(1));

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
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(1,"Task 1", "Description 1", Duration.ofHours(2), startTime);
        manager.createTask(task);

        List<Task> tasks = manager.getAllTasks();
        System.out.println(tasks);

        boolean removed = manager.deleteTaskById(task.getId());
        assertTrue(removed);

        boolean removedTask = manager.deleteTaskById(task.getId());
        assertFalse(removedTask);
    }

    @Test
    void shouldRemoveEpicById() {
        Epic epic = new Epic(1001,"Epic 1", "Description 1");
        manager.createEpic(epic);

        boolean removed = manager.deleteEpicById(epic.getId());
        assertTrue(removed);

        boolean removedEpic = manager.deleteEpicById(epic.getId());
        assertFalse(removedEpic);
    }

    @Test
    void shouldRemoveSubtaskById() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask(101, "Test Subtask", "Test description", epic.getId(), Duration.ofHours(2), startTime);
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        boolean removed = manager.deleteSubtaskById(subtask.getId());
        assertTrue(removed);

        boolean removedSubtask = manager.deleteSubtaskById(subtask.getId());
        assertFalse(removedSubtask);
    }

    @Test
    void shouldAddTaskWithUniqueId() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(1), startTime);
        Task task2 = new Task(2,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime.plusHours(1));

        assertDoesNotThrow(() -> manager.createTask(task1));
        assertDoesNotThrow(() -> manager.createTask(task2));

        assertEquals(task1, manager.getTaskById(1));
        assertEquals(task2, manager.getTaskById(2));
    }

    @Test
    void shouldAddTasksWithDifferentsId() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(1), startTime);
        Task task2 = new Task(2,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime.plusHours(1));

        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void shouldEqualsIdOfSameTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = new Task(1,"Test addNewTask1", "Test addNewTask description1", Duration.ofHours(1), startTime);
        Task task2 = new Task(1,"Test addNewTask2", "Test addNewTask description2", Duration.ofHours(2), startTime.plusHours(1));

        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void shouldReturnNullIfNotExistTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime);
        manager.createTask(task1);

        assertNull(manager.getTaskById(2));
    }

    @Test
    void shouldAddEpicAndSubtaskWithDifferentId() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic1 = new Epic(1,"Test addNewEpic1", "Test addNewEpic description1");
        Epic epic2 = new Epic(1,"Test addNewEpic2", "Test addNewEpic description2");

        Subtask subtask1 = new Subtask(2, "Test Subtask1", "Test description1",1, Duration.ofHours(1), startTime);
        Subtask subtask2 = new Subtask(3, "Test Subtask2", "Test description2",1, Duration.ofHours(2), startTime.plusHours(1));

        assertNotEquals(epic1.hashCode(), epic2.hashCode());
        assertNotEquals(subtask1.hashCode(), subtask2.hashCode());
    }
}