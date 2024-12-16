package com.yandex.app.test;

import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setup() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldReturnInitializedTaskManagerGetDefault() {
        assertNotNull(taskManager, "TaskManager не может быть null");
    }

    @Test
    void shouldReturnInitializedHistoryManagerGetDefaultHistory() {
        assertNotNull(historyManager, "HistoryManager не может быть null");
    }

    @Test
    void shouldReturnSameInstanceOfHistoryManager() {
        HistoryManager historyManager1 = Managers.getDefaultHistory();
        HistoryManager historyManager2 = Managers.getDefaultHistory();
        assertEquals(historyManager1, historyManager2);
    }
}