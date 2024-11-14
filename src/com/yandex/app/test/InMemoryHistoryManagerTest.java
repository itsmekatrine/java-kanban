package com.yandex.app.test;

import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setup() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddAndSaveTaskInHistory() {
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description");
        historyManager.updateHistory(task);

        Task currentTask = historyManager.getCurrentTask(1);
        assertNotNull(currentTask);
        assertEquals(task, currentTask);
    }

    @Test
    void shouldEqualsTaskWithLastVersionTask() {
        Task firstVersion = new Task(1,"Test addNewTask1", "Test addNewTask description1");
        Task secondVersion = new Task(1,"Test addNewTask2", "Test addNewTask description2");

        historyManager.updateHistory(firstVersion);
        historyManager.updateHistory(secondVersion);

        Task lastTask = historyManager.getLastSavedTask(1);
        assertEquals(secondVersion, lastTask);
    }

    @Test
    void shouldReturnNullIfTaskNotExist() {
        assertNull(historyManager.getCurrentTask(1));
    }

}