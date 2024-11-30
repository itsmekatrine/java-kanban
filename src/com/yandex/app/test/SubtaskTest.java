package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private TaskManager manager;
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private Subtask subtask;
    private Epic epic;

    @BeforeEach
    public void setup() {
        manager = new InMemoryTaskManager();
        epic = new Epic(1001, "Test Epic", "Test description");
        subtask = new Subtask(101, "Test addNewSubtask", "Test addNewSubtask description", epic.getId());
    }

    @Test
    public void shouldDeletedSubtaskNotContainsId() {
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask(101,"Test Subtask 1", "Test description 1", epic.getId());
        Subtask subtask2 = new Subtask(102,"Test Subtask 2", "Test description 2", epic.getId());

        manager.createSubtask(epic.getId(), subtask1);
        manager.createSubtask(epic.getId(), subtask2);

        List<Integer> existSubtasksIds = manager.getEpicById(epic.getId()).getSubtaskIds();
        assertTrue(existSubtasksIds.contains(subtask1.getId()));

        manager.deleteSubtaskFromEpic(subtask1.getId());
        assertNull(manager.getSubtaskById(subtask1.getId()), "Удалённая подзадача должна быть null.");
        assertNotNull(manager.getSubtaskById(subtask2.getId()), "Вторая подзадача существует.");

        List<Integer> deletedSubtasksIds = manager.getEpicById(epic.getId()).getSubtaskIds();
        assertFalse(deletedSubtasksIds.contains(subtask1.getId()));

        historyManager.updateHistory(epic);
        historyManager.updateHistory(subtask2);
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void addNewSubtask() {
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
        Subtask subtask1 = new Subtask(101,"Test Subtask 1", "Test description 1",2);
        Subtask subtask2 = new Subtask(101,"Test Subtask 2", "Test description 2",2);
        assertTrue(subtask1.equals(subtask2));

        Subtask subtask3 = new Subtask(102,"Test Subtask 3", "Test description 3",5);
        assertFalse(subtask1.equals(subtask3));
    }
}