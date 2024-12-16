package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.FileBackedTaskManager;
import com.yandex.app.service.InMemoryHistoryManager;
import com.yandex.app.service.Managers;
import org.junit.jupiter.api.*;
import java.io.File;

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
        manager.createTask(new Task(1, "Task 1", "Description 1"));
        manager.createTask(new Task(2, "Task 2", "Description 2"));
        manager.createEpic(new Epic(1001, "Epic 1", "Description 1"));
        manager.createSubtask(1001, new Subtask(101, "Subtask 1", "Description 1", 1001));

        manager.save();
        assertTrue(savedFile.exists(), "File should be created.");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedFile, new InMemoryHistoryManager());

        Assertions.assertEquals(2, loadedManager.getAllTasks().size());
        Assertions.assertEquals(1, loadedManager.getAllEpics().size());
        Assertions.assertEquals(1, loadedManager.getAllSubtasks().size());
    }

    @Test
    public void shouldLoadTasks() {
        manager.createTask(new Task(1, "Task 1", "Description 1"));
        manager.createTask(new Task(2, "Task 2", "Description 2"));

        manager.save();
        assertTrue(savedFile.exists(), "File should be created.");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedFile, new InMemoryHistoryManager());

        Assertions.assertEquals(2, loadedManager.getAllTasks().size());
    }
}
