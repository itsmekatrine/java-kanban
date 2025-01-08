import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    void setup() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        historyManager.clearHistory();
    }

    @Test
    void shouldAddAndSaveTaskInHistory() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime);
        historyManager.updateHistory(task);

        Task currentTask = historyManager.getCurrentTask(1);
        assertNotNull(currentTask);
        assertEquals(task, currentTask);
    }

    @Test
    void shouldEqualsTaskWithLastVersionTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Task firstVersion = new Task(1,"Test addNewTask1", "Test addNewTask description1", Duration.ofHours(1), startTime);
        Task secondVersion = new Task(1,"Test addNewTask2", "Test addNewTask description2", Duration.ofHours(2), startTime.plusHours(1));

        historyManager.updateHistory(firstVersion);
        historyManager.updateHistory(secondVersion);

        Task lastTask = historyManager.getLastSavedTask(1);
        assertEquals(secondVersion, lastTask);
    }

    @Test
    void shouldReturnNullIfTaskNotExist() {
        assertNull(historyManager.getCurrentTask(1));
    }

    @Test
    void shouldHistoryWithoutDuplicates() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(10), startTime);
        Task task2 = new Task(2, "Task 2", "Description 2", Duration.ofMinutes(15), startTime.plusMinutes(30));
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic(1001, "Epic 1", "Epic Description");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask(101, "Subtask 1", "Description 1", epic1.getId(), Duration.ofHours(1), startTime.plusHours(1));
        Subtask subtask2 = new Subtask(102, "Subtask 2", "Description 2", epic1.getId(), Duration.ofHours(1), startTime.plusHours(4));
        Subtask subtask3 = new Subtask(103, "Subtask 3", "Description 3", epic1.getId(), Duration.ofHours(1), startTime.plusHours(6));

        manager.createSubtask(epic1.getId(), subtask1);
        manager.createSubtask(epic1.getId(), subtask2);
        manager.createSubtask(epic1.getId(), subtask3);

        Epic epic2 = new Epic(1002, "Epic 2", "Epic Description");
        manager.createEpic(epic2);

        manager.getTaskById(2);
        manager.getTaskById(1);
        manager.getEpicById(1002);
        manager.getSubtaskById(102);
        manager.getSubtaskById(101);
        manager.getSubtaskById(103);
        manager.getEpicById(1001);

        List<Task> history = historyManager.getHistory();
        System.out.println("История первого запроса: " + historyManager.getHistory());
        assertEquals(7, history.size(), "История не содержит дубликатов.");
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertTrue(history.contains(subtask1));
        assertTrue(history.contains(subtask2));
        assertTrue(history.contains(subtask3));
        assertTrue(history.contains(epic1));
        assertTrue(history.contains(epic2));

        manager.getEpicById(1002);
        manager.getTaskById(2);
        manager.getEpicById(1001);
        manager.getSubtaskById(103);
        manager.getSubtaskById(102);
        manager.getSubtaskById(101);
        manager.getTaskById(1);

        System.out.println("История второго запроса: " + historyManager.getHistory());
        assertEquals(7, history.size(), "История не содержит дубликатов.");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        LocalDateTime startTime = LocalDateTime.now();
        manager.createTask(new Task(1, "Task 1", "Description 1", Duration.ofHours(1), startTime));
        manager.createTask(new Task(2, "Task 2", "Description 2", Duration.ofHours(2), startTime.plusHours(1)));

        List<Task> historyBeforeRemoving = historyManager.getHistory();
        System.out.println("История запроса до удаления: " + historyBeforeRemoving);
        assertEquals(2, historyBeforeRemoving.size(), "В истории 2 задачи");

        manager.deleteTaskById(1);

        List<Task> historyAfterRemoving = historyManager.getHistory();
        System.out.println("История запроса после удаления: " + historyAfterRemoving);
        assertEquals(1, historyAfterRemoving.size(), "В истории осталась 1 задача");
    }

    @Test
    void shouldRemoveEpicFromHistory() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic1 = new Epic(1001, "Epic 1", "Epic Description");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask(101, "Subtask 1", "Description 1", epic1.getId(), Duration.ofHours(1), startTime);
        Subtask subtask2 = new Subtask(102, "Subtask 2", "Description 2", epic1.getId(), Duration.ofHours(2), startTime.plusHours(2));
        Subtask subtask3 = new Subtask(103, "Subtask 3", "Description 3", epic1.getId(), Duration.ofHours(3), startTime.plusHours(4));

        manager.createSubtask(epic1.getId(), subtask1);
        manager.createSubtask(epic1.getId(), subtask2);
        manager.createSubtask(epic1.getId(), subtask3);

        List<Task> historyBeforeRemoving = historyManager.getHistory();
        System.out.println("История запроса до удаления: " + historyBeforeRemoving);
        assertEquals(4, historyBeforeRemoving.size(), "В истории 3 задачи и 1 эпик");

        manager.deleteEpicById(1001);

        List<Task> historyAfterRemoving = historyManager.getHistory();
        System.out.println("История запроса после удаления: " + historyAfterRemoving);
        assertEquals(0, historyAfterRemoving.size(), "В истории пусто");
    }
}