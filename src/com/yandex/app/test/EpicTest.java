package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void addNewEpic() {
        TaskManager manager = Managers.getDefault();

        Epic epic = new Epic(1,"Test addNewEpic 1", "Test addNewEpic description 1");
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
        Epic epic1 = new Epic(1,"Test Epic 1", "Test description 1");
        Epic epic2 = new Epic(1,"Test Epic 2", "Test description 2");
        assertTrue(epic1.equals(epic2));

        Epic epic3 = new Epic(2,"Test Epic 3", "Test description 3");
        assertFalse(epic1.equals(epic3));
    }

    @Test
    void shouldEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic(1,"Test Epic 1", "Test description 1");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Subtask subtask = new Subtask(1,"Test Subtask 1", "Test description 1",epic.getId());
        });
    }
}