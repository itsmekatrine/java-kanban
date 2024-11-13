package com.yandex.app.test;

import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    private Managers managers;

    @BeforeEach
    void setup() {
        managers = new Managers();
    }

    @Test
    void shouldReturnInitializedTaskManagerGetDefault() {
        TaskManager taskManager = managers.getDefault();
        assertNotNull(taskManager, "TaskManager не может быть null");
    }

    @Test
    void shouldReturnInitializedHistoryManagerGetDefaultHistory() {
        HistoryManager historyManager = managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не может быть null");
    }

    @Test
    void shouldReturnSameInstanceOfTaskManager() {
        TaskManager taskManager1 = managers.getDefault();
        TaskManager taskManager2 = managers.getDefault();
        assertEquals(taskManager1, taskManager2);
    }

    @Test
    void shouldReturnSameInstanceOfHistoryManager() {
        HistoryManager historyManager1 = managers.getDefaultHistory();
        HistoryManager historyManager2 = managers.getDefaultHistory();
        assertEquals(historyManager1, historyManager2);
    }
}