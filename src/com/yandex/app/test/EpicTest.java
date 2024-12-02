package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager manager;
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void setup() {
        manager = new InMemoryTaskManager();
        epic = new Epic(1001, "Test Epic", "Test description");
        subtask = new Subtask(101, "Test addNewSubtask", "Test addNewSubtask description", epic.getId());
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic(1001,"Test addNewEpic 1", "Test addNewEpic description 1");
        final int epicId = manager.createEpic(epic);

        final Task savedEpic = manager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void shouldEqualityEpicsById() {
        Epic epic1 = new Epic(1001,"Test Epic 1", "Test description 1");
        Epic epic2 = new Epic(1001,"Test Epic 2", "Test description 2");
        assertTrue(epic1.equals(epic2));

        Epic epic3 = new Epic(1002,"Test Epic 3", "Test description 3");
        assertFalse(epic1.equals(epic3));
    }

    @Test
    void shouldEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic(1001,"Test Epic 1", "Test description 1");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
        Subtask subtask = new Subtask(1001,"Test Subtask 1", "Test description 1",epic.getId());
        });
    }

    @Test
    public void shouldUpdateEpicStatusAfterDeletingSubtask() {
        Epic epic = new Epic(1001,"Test addNewEpic 1", "Test addNewEpic description 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(101, "Test addNewSubtask", "Test addNewSubtask description", epic.getId());
        manager.createSubtask(epic.getId(), subtask);
        assertEquals(1, epic.getSubtasks().size());

        manager.deleteSubtaskFromEpic(subtask.getId());
        assertTrue(epic.getSubtasks().isEmpty());

        assertEquals(StatusTask.NEW, epic.getStatus(), "Эпик должен иметь статус NEW, когда нет активных подзадач.");

        historyManager.updateHistory(epic);
        assertEquals(1, historyManager.getHistory().size());
    }
}