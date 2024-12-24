package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.FileBackedTaskManager;
import com.yandex.app.service.InMemoryHistoryManager;
import com.yandex.app.service.Managers;
import org.junit.jupiter.api.*;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File savedFile;

    @BeforeEach
    public void setup() throws Exception {
        savedFile = new File("tasks.csv");
        manager = (FileBackedTaskManager) Managers.getDefault();
    }

    @AfterEach
    public void tearDown() {
        if (savedFile.exists()) {
            savedFile.delete();
        }
    }

    @Test
    public void shouldSaveAndLoadEmptyFile() {
        manager.save();
        assertTrue(savedFile.exists(), "File should be created.");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedFile, new InMemoryHistoryManager());

        Assertions.assertEquals(0, loadedManager.getAllTasks().size());
        Assertions.assertEquals(0, loadedManager.getAllSubtasks().size());
        Assertions.assertEquals(0, loadedManager.getAllEpics().size());
    }

    @Test
    public void shouldSaveTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        manager.createTask(new Task(1, "Task 1", "Description 1", Duration.ofMinutes(30), startTime));
        manager.createTask(new Task(2, "Task 2", "Description 2", Duration.ofMinutes(60), startTime.plusHours(1)));
        manager.createEpic(new Epic(1001, "Epic 1", "Description 1"));
        manager.createSubtask(1001, new Subtask(101, "Subtask 1", "Description 1", 1001, Duration.ofMinutes(180), startTime.plusHours(3)));

        manager.save();
        assertTrue(savedFile.exists(), "File should be created.");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedFile, new InMemoryHistoryManager());

        Assertions.assertEquals(2, loadedManager.getAllTasks().size());
        Assertions.assertEquals(1, loadedManager.getAllEpics().size());
        Assertions.assertEquals(1, loadedManager.getAllSubtasks().size());
    }

    @Test
    public void shouldLoadTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        manager.createTask(new Task(1, "Task 1", "Description 1", Duration.ofMinutes(30), startTime));
        manager.createTask(new Task(2, "Task 2", "Description 2", Duration.ofMinutes(60), startTime.plusHours(1)));

        manager.save();
        assertTrue(savedFile.exists(), "File should be created.");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedFile, new InMemoryHistoryManager());

        Assertions.assertEquals(2, loadedManager.getAllTasks().size());
    }

    @Test
    public void shouldSaveDurationAndStartTime() {
        Duration duration = Duration.ofMinutes(120);
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 24, 18, 0);
        Task task = new Task(1, "Task 1", "Description 1", duration, startTime);

        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals("Task 1", task.getTitle());
        Assertions.assertEquals("Description 1", task.getDescription());
        Assertions.assertEquals(duration, task.getDuration());
        Assertions.assertEquals(startTime, task.getStartTime());
    }

    @Test
    public void shouldTaskToString() {
        Duration duration = Duration.ofMinutes(120);
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 24, 18, 0);
        Task task = new Task(1, "Task 1", "Description 1", duration, startTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd–MM–yy | HH:mm");
        String expectedString = String.format("1,TASK,Task 1,NEW,Description 1,%d,%s", duration.toMinutes(), startTime.format(formatter));

        Assertions.assertEquals(expectedString, task.toString());
    }

    @Test
    public void shouldTaskFromString() {
        String input = "1,TASK,Task 1,NEW,Description 1,120,24–12–24 | 18:00";
        Task task = manager.fromString(input);

        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals("Task 1", task.getTitle());
        Assertions.assertEquals("Description 1", task.getDescription());
        Assertions.assertEquals(Duration.ofMinutes(120), task.getDuration());

        LocalDateTime expectedStartTime = LocalDateTime.of(2024, 12, 24, 18, 0);
        Assertions.assertEquals(expectedStartTime, task.getStartTime());
    }
}
