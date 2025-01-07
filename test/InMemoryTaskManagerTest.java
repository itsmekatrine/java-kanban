import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        return (InMemoryTaskManager) taskManager;
    }

    @BeforeEach
    void setup() {
        manager = createTaskManager();
    }

    @Test
    void shouldAddTaskWithUniqueId() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(1), startTime);
        Task task2 = new Task(2,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime.plusHours(1));

        assertDoesNotThrow(() -> manager.createTask(task1));
        assertDoesNotThrow(() -> manager.createTask(task2));

        assertEquals(task1, manager.getTaskById(1));
        assertEquals(task2, manager.getTaskById(2));
    }

    @Test
    void shouldAddTasksWithDifferentsId() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(1), startTime);
        Task task2 = new Task(2,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime.plusHours(1));

        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void shouldEqualsIdOfSameTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = new Task(1,"Test addNewTask1", "Test addNewTask description1", Duration.ofHours(1), startTime);
        Task task2 = new Task(1,"Test addNewTask2", "Test addNewTask description2", Duration.ofHours(2), startTime.plusHours(1));

        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void shouldReturnNullIfNotExistTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task1 = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime);
        manager.createTask(task1);

        assertNull(manager.getTaskById(2));
    }

    @Test
    void shouldAddEpicAndSubtaskWithDifferentId() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic1 = new Epic(1,"Test addNewEpic1", "Test addNewEpic description1");
        Epic epic2 = new Epic(1,"Test addNewEpic2", "Test addNewEpic description2");

        Subtask subtask1 = new Subtask(2, "Test Subtask1", "Test description1",1, Duration.ofHours(1), startTime);
        Subtask subtask2 = new Subtask(3, "Test Subtask2", "Test description2",1, Duration.ofHours(2), startTime.plusHours(1));

        assertNotEquals(epic1.hashCode(), epic2.hashCode());
        assertNotEquals(subtask1.hashCode(), subtask2.hashCode());
    }
}