package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    TaskManager manager = new InMemoryTaskManager();
    HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void addNewSubtask() {
        Epic epic = new Epic(1,"Test Epic", "Test description");
        Subtask subtask = new Subtask(2,"Test addNewSubtask", "Test addNewSubtask description", epic.getId());
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);
        historyManager.updateHistory(epic);
        historyManager.updateHistory(subtask);

        final Task savedSubtask = manager.getSubtaskById(subtask.getId());

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = manager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void shouldEqualitySubtasksById() {
        Subtask subtask1 = new Subtask(1,"Test Subtask 1", "Test description 1",2);
        Subtask subtask2 = new Subtask(1,"Test Subtask 2", "Test description 2",2);
        assertTrue(subtask1.equals(subtask2));

        Subtask subtask3 = new Subtask(3,"Test Subtask 3", "Test description 3",5);
        assertFalse(subtask1.equals(subtask3));
    }
}