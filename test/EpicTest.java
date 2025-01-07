import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager manager;
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void setup() {
        LocalDateTime startTime = LocalDateTime.now();
        manager = Managers.getDefault();
        epic = new Epic(1001, "Test Epic", "Test description");
        subtask = new Subtask(101, "Test addNewSubtask", "Test addNewSubtask description", epic.getId(), Duration.ofHours(2), startTime);
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
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Test Epic 1", "Test description 1");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Subtask subtask = new Subtask(1001,"Test Subtask 1", "Test description 1",epic.getId(), Duration.ofHours(2), startTime);
        });
    }

    @Test
    public void shouldUpdateEpicStatusAfterDeletingSubtask() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Epic 1", "Description 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask(101,"Subtask 1", "Description 1", epic.getId(), Duration.ofHours(1), startTime);
        Subtask subtask2 = new Subtask(102,"Subtask 2", "Description 2", epic.getId(), Duration.ofHours(2), startTime.plusHours(2));
        manager.createSubtask(epic.getId(), subtask1);
        manager.createSubtask(epic.getId(), subtask2);
        assertEquals(2, epic.getSubtasks().size());
        manager.deleteSubtaskFromEpic(subtask1.getId());
        assertEquals(1, epic.getSubtasks().size());

        assertEquals(StatusTask.NEW, epic.getStatus(), "Эпик должен иметь статус NEW, когда нет активных подзадач.");

        historyManager.updateHistory(epic);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void shouldChangeStatusEpic() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Epic 1", "Epic 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask(101,"Subtask 1", "Description 1", epic.getId(), Duration.ofHours(1), startTime);
        Subtask subtask2 = new Subtask(102,"Subtask 2", "Description 2", epic.getId(), Duration.ofHours(2), startTime.plusHours(2));
        manager.createSubtask(epic.getId(), subtask1);
        manager.createSubtask(epic.getId(), subtask2);

        assertEquals(StatusTask.NEW, epic.getStatus(), "Эпик должен иметь статус NEW, когда нет активных подзадач.");
        System.out.println("Current subtask 1 status: " + subtask1.getStatus());
        System.out.println("Current subtask 2 status: " + subtask2.getStatus());
        System.out.println("Current epic status: " + epic.getStatus());

        System.out.println();

        subtask1.setStatus(StatusTask.IN_PROGRESS);
        epic.updateEpicStatus();
        System.out.println("Update subtask 1 status: " + subtask1.getStatus());
        System.out.println("Current subtask 2 status: " + subtask2.getStatus());
        System.out.println("Update epic status: " + epic.getStatus());
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatus(), "Эпик должен иметь статус IN PROGRESS, когда есть хотя бы одна активная подзадача.");

        System.out.println();

        subtask1.setStatus(StatusTask.DONE);
        subtask2.setStatus(StatusTask.NEW);
        epic.updateEpicStatus();
        System.out.println("Update subtask 1 status: " + subtask1.getStatus());
        System.out.println("Update subtask 2 status: " + subtask2.getStatus());
        System.out.println("Update epic status: " + epic.getStatus());
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatus(), "Эпик должен иметь статус IN PROGRESS, когда когда одна подзадача завершена, а другая ещё не начата.");

        System.out.println();

        subtask1.setStatus(StatusTask.DONE);
        subtask2.setStatus(StatusTask.DONE);
        epic.updateEpicStatus();
        System.out.println("Update subtask 1 status: " + subtask1.getStatus());
        System.out.println("Update subtask 2 status: " + subtask2.getStatus());
        System.out.println("Update epic status: " + epic.getStatus());
        assertEquals(StatusTask.DONE, epic.getStatus(), "Эпик должен иметь статус DONE, когда все подзадачи завершены.");
    }
}