import com.yandex.app.model.Task;
import com.yandex.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    public void setup() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        historyManager.clearHistory();
    }

    @Test
    void addNewTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description", duration, startTime);
        final int taskId = manager.createTask(task);
        historyManager.updateHistory(task);

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void add() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Task task = new Task(1,"Test addTask", "Test addTask description", duration, startTime);
        historyManager.updateHistory(task);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldEqualityTasksById() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration1 = Duration.ofHours(1);
        Duration duration2 = Duration.ofHours(2);
        Duration duration3 = Duration.ofHours(4);
        Task task1 = new Task(1,"Test Task 1", "Test description 1", duration1, startTime);
        Task task2 = new Task(1,"Test Task 2", "Test description 2", duration2, startTime.plusHours(1));
        assertTrue(task1.equals(task2));

        Task task3 = new Task(2,"Test Task 3", "Test description 3", duration3, startTime.plusHours(2));
        assertFalse(task1.equals(task3));
    }

    @Test
    void shouldChangeTitleInTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        Task task = new Task(1,"Task 1", "Test description 1", duration, startTime);
        manager.createTask(task);

        System.out.println(task);
        System.out.println("Tasks count: " + manager.getAllTasks().size());

        Task updateTask = new Task(task.getId(), "Task 2", task.getDescription(), task.getDuration(), task.getStartTime());
        System.out.println("Updating task with ID: " + updateTask.getId());
        manager.updateTask(updateTask.getId(), task);

        System.out.println(updateTask);

        Task requestedTask = manager.getTaskById(task.getId());
        System.out.println("Requested Task: " + requestedTask);
        assertEquals("Task 1", requestedTask.getTitle());
    }

    @Test
    void shouldChangeDescriptionInTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        Task task = new Task(1,"Test Task 1", "Test description 1", duration, startTime);
        manager.createTask(task);

        Task updateTask = new Task(task.getId(), task.getTitle(), "Test description 2", task.getDuration(), task.getStartTime());
        manager.updateTask(updateTask.getId(), task);

        Task requestedTask = manager.getTaskById(1);
        assertEquals("Test description 1", requestedTask.getDescription());
    }


}