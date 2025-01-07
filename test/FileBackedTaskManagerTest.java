import com.yandex.app.exception.ManagerSaveException;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.FileBackedTaskManager;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.InMemoryHistoryManager;
import com.yandex.app.service.Managers;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File savedFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return (FileBackedTaskManager) Managers.getDefault();
    }

    @BeforeEach
    public void setup() throws Exception {
        super.setup();
        savedFile = new File("tasks.csv");
        manager = (FileBackedTaskManager) Managers.getDefault();
    }

    @AfterEach
    public void tearDown() {
        if (savedFile.exists()) {
            savedFile.delete();
        }
    }

    @Test
    public void shouldSaveAndLoadEmptyFile() {
        manager.save();
        assertTrue(savedFile.exists(), "File should be created.");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedFile, new InMemoryHistoryManager());

        Assertions.assertEquals(0, loadedManager.getAllTasks().size());
        Assertions.assertEquals(0, loadedManager.getAllSubtasks().size());
        Assertions.assertEquals(0, loadedManager.getAllEpics().size());
    }

    @Test
    public void shouldSaveTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        manager.createTask(new Task(1, "Task 1", "Description 1", Duration.ofMinutes(30), startTime));
        manager.createTask(new Task(2, "Task 2", "Description 2", Duration.ofMinutes(60), startTime.plusHours(1)));
        manager.createEpic(new Epic(1001, "Epic 1", "Description 1"));
        manager.createSubtask(1001, new Subtask(101, "Subtask 1", "Description 1", 1001, Duration.ofMinutes(180), startTime.plusHours(3)));

        manager.save();
        assertTrue(savedFile.exists(), "File should be created.");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedFile, new InMemoryHistoryManager());

        Assertions.assertEquals(2, loadedManager.getAllTasks().size());
        Assertions.assertEquals(1, loadedManager.getAllEpics().size());
        Assertions.assertEquals(1, loadedManager.getAllSubtasks().size());
    }

    @Test
    public void shouldLoadTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        manager.createTask(new Task(1, "Task 1", "Description 1", Duration.ofMinutes(30), startTime));
        manager.createTask(new Task(2, "Task 2", "Description 2", Duration.ofMinutes(60), startTime.plusHours(1)));

        manager.save();
        assertTrue(savedFile.exists(), "File should be created.");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedFile, new InMemoryHistoryManager());

        Assertions.assertEquals(2, loadedManager.getAllTasks().size());
    }

    @Test
    public void shouldSaveDurationAndStartTime() {
        Duration duration = Duration.ofMinutes(120);
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 24, 18, 0);
        Task task = new Task(1, "Task 1", "Description 1", duration, startTime);

        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals("Task 1", task.getTitle());
        Assertions.assertEquals("Description 1", task.getDescription());
        Assertions.assertEquals(duration, task.getDuration());
        Assertions.assertEquals(startTime, task.getStartTime());
    }

    @Test
    public void shouldTaskToString() {
        Duration duration = Duration.ofMinutes(120);
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 24, 18, 0);
        Task task = new Task(1, "Task 1", "Description 1", duration, startTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd–MM–yy | HH:mm");
        String expectedString = String.format("1,TASK,Task 1,NEW,Description 1,%d,%s", duration.toMinutes(), startTime.format(formatter));

        Assertions.assertEquals(expectedString, task.toString());
    }

    @Test
    public void shouldTaskFromString() {
        String input = "1,TASK,Task 1,NEW,Description 1,120,24–12–24 | 18:00";
        Task task = manager.fromString(input);

        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals("Task 1", task.getTitle());
        Assertions.assertEquals("Description 1", task.getDescription());
        Assertions.assertEquals(Duration.ofMinutes(120), task.getDuration());

        LocalDateTime expectedStartTime = LocalDateTime.of(2024, 12, 24, 18, 0);
        Assertions.assertEquals(expectedStartTime, task.getStartTime());
    }

    @Test
    public void shouldSortedTasksByStartTime() {
        Task task1 = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.of(2024, 10, 1, 10, 0));
        Task task2 = new Task(2, "Task 2", "Description 2", Duration.ofMinutes(15), LocalDateTime.of(2024, 10, 1, 12, 0));
        Epic epic = new Epic(1001,"Test Epic 1", "Test description 1");
        Subtask subtask3 = new Subtask(101, "Subtask 3", "Description 3", 1001, Duration.ofMinutes(20), LocalDateTime.of(2024, 9, 30, 10, 0));
        Subtask subtask4 = new Subtask(102, "Subtask 4", "Description 4", 1001, Duration.ofMinutes(25), null);
        Task task5 = new Task(5, "Task 5", "Description 5", Duration.ofMinutes(30), LocalDateTime.of(2023, 10, 1, 9, 0));

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        manager.createSubtask(1001, subtask3);
        manager.createSubtask(1001, subtask4);
        manager.createTask(task5);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        System.out.println(prioritizedTasks);

        Assertions.assertEquals(task5, prioritizedTasks.get(0));
        Assertions.assertEquals(subtask3, prioritizedTasks.get(1));
        Assertions.assertEquals(task1, prioritizedTasks.get(2));
        Assertions.assertEquals(task2, prioritizedTasks.get(3));
        Assertions.assertEquals(subtask4, prioritizedTasks.get(4));
    }

    @Test
    public void shouldCheckCrossIntervals() {
        Task task1 = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(60), LocalDateTime.of(2024, 12, 24, 10, 0));
        Task task2 = new Task(2, "Task 2", "Description 2", Duration.ofMinutes(60), LocalDateTime.of(2024, 12, 24, 12, 0));
        Task task3 = new Task(3, "Task 3", "Description 3", Duration.ofMinutes(60), LocalDateTime.of(2024, 12, 24, 11, 30));

        manager.createTask(task1);
        manager.createTask(task2);

        System.out.println("Task 1: " + task1.getStartTime() + " - " + task1.getEndTime());
        System.out.println("Task 2: " + task2.getStartTime() + " - " + task2.getEndTime());

        Assertions.assertEquals(2, manager.getAllTasks().size(), "Количество задач не совпадает с ожидаемым.");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            manager.createTask(task3);
        });

        Assertions.assertEquals("Есть пересечение с другой задачей", exception.getMessage());

        Assertions.assertEquals(2, manager.getAllTasks().size(), "Количество задач изменилось после попытки добавить пересекающуюся задачу.");
    }

    @Test
    void shouldSaveTasksToFile() throws IOException {
        Task task = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(60), LocalDateTime.of(2024, 12, 24, 10, 0));
        Epic epic = new Epic(1001, "Epic 1", "Epic Description");
        Subtask subtask = new Subtask(101, "Subtask 1", "Subtask Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.of(2024, 12, 24, 12, 0));

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        manager.save();

        String fileContent = Files.readString(savedFile.toPath(), StandardCharsets.UTF_8);
        System.out.println("File content: \n" + fileContent);

        Assertions.assertTrue(fileContent.contains(task.toString()), "Task not saved correctly.");
        Assertions.assertTrue(fileContent.contains(epic.toString()), "Epic not saved correctly.");
        Assertions.assertTrue(fileContent.contains(subtask.toString()), "Subtask not saved correctly.");
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenFileCannotBeRead() {
        File nonExistentFile = new File("nonexistentfile.csv");

        Exception exception = Assertions.assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile, new InMemoryHistoryManager())
        );

        Assertions.assertTrue(exception.getMessage().contains("Ошибка чтения файла"), "Expected ManagerSaveException to be thrown with the correct message.");
    }
}
