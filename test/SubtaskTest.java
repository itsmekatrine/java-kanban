import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private TaskManager manager;
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private Subtask subtask;
    private Epic epic;

    @BeforeEach
    public void setup() {
        manager = Managers.getDefault();
        epic = new Epic(1001, "Test Epic", "Test description");
        subtask = new Subtask(101, "Test addNewSubtask", "Test addNewSubtask description", epic.getId(), Duration.ofHours(2), LocalDateTime.now());
        historyManager.clearHistory();
    }

    @Test
    public void shouldDeletedSubtaskNotContainsId() {
        LocalDateTime startTime = LocalDateTime.now();
        epic.setStartTime(startTime);
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask(101,"Test Subtask 1", "Test description 1", epic.getId(), Duration.ofHours(1), startTime.plusHours(1));
        Subtask subtask2 = new Subtask(102,"Test Subtask 2", "Test description 2", epic.getId(), Duration.ofHours(2), startTime.plusHours(2));

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
        LocalDateTime startTime = LocalDateTime.now();
        epic.setStartTime(startTime);
        Subtask subtask1 = new Subtask(101,"Test Subtask 1", "Test description 1",2, Duration.ofHours(1), startTime.plusHours(1));
        Subtask subtask2 = new Subtask(101,"Test Subtask 2", "Test description 2",2, Duration.ofHours(2), startTime.plusHours(2));
        assertTrue(subtask1.equals(subtask2));

        Subtask subtask3 = new Subtask(102,"Test Subtask 3", "Test description 3",5, Duration.ofHours(3), startTime.plusHours(3));
        assertFalse(subtask1.equals(subtask3));
    }

    @Test
    void shouldCheckSubtasksInEpic() {
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        final List<Subtask> epicSubtasks = manager.getAllSubtasksOfEpic(epic.getId());

        assertNotNull(epicSubtasks, "Список подзадач эпика не возвращается.");
        assertEquals(1, epicSubtasks.size(), "Неверное количество подзадач у эпика.");
        assertEquals(subtask, epicSubtasks.get(0), "Подзадача не содержится в эпике.");
    }

    @Test
    void shouldCheckEpicExistenceForSubtask() {
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        Epic savedEpic = manager.getEpicById(subtask.getEpicId());
        assertNotNull(savedEpic, "Эпик для подзадачи не найден.");
        assertEquals(epic, savedEpic, "Эпик не соответствует ожидаемому.");

        assertEquals(epic.getId(), subtask.getEpicId(), "ID эпика в подзадаче не совпадает с ожидаемым.");
    }
}