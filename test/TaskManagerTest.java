import com.yandex.app.exception.NotFoundException;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @BeforeEach
    void setup() throws Exception {
        manager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    public void shouldAddTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(30), startTime);
        manager.createTask(task);

        Task existTask = manager.getTaskById(task.getId());
        Assertions.assertEquals(task, existTask);

        List<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.contains(task));
        assertEquals(1, tasks.size());
    }

    @Test
    public void shouldAddEpic() {
        Epic epic = new Epic(1001,"Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);

        Epic foundEpic = manager.getEpicById(epic.getId());
        assertEquals(epic, foundEpic);

        List<Epic> tasks = manager.getAllEpics();
        assertTrue(tasks.contains(epic));
        assertEquals(1, tasks.size());
    }

    @Test
    public void shouldAddSubtask() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask(101,"Test Subtask", "Test description", epic.getId(), Duration.ofHours(2), startTime);
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        Subtask foundSubtask = manager.getSubtaskById(subtask.getId());
        assertEquals(subtask, foundSubtask);

        List<Subtask> tasks = manager.getAllSubtasks();

        assertTrue(tasks.contains(subtask));
        assertEquals(1, tasks.size());
    }

    @Test
    void shouldGetTaskById() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(1,"Test addNewTask", "Test addNewTask description", Duration.ofHours(2), startTime);
        manager.createTask(task);

        Task foundTask = manager.getTaskById(task.getId());
        assertEquals(task, foundTask);
    }

    @Test
    void shouldGetEpicById() {
        Epic epic = new Epic(1001,"Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);

        Epic foundEpic = manager.getEpicById(epic.getId());
        assertEquals(epic, foundEpic);
    }

    @Test
    void shouldGetSubtaskById() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask(101,"Test Subtask", "Test description", epic.getId(), Duration.ofHours(2), startTime);
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        Subtask foundSubtask = manager.getSubtaskById(subtask.getId());
        assertEquals(subtask, foundSubtask);
    }

    @Test
    void shouldGetSubtasksByEpic() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Test addNewEpic 1", "Test addNewEpic description 1");
        Subtask subtask1 = new Subtask(101, "Test Subtask 1", "Test description 1",epic.getId(), Duration.ofHours(1), startTime);
        Subtask subtask2 = new Subtask(102, "Test Subtask 2", "Test description 2",epic.getId(), Duration.ofHours(2), startTime.plusHours(1));

        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask1);
        manager.createSubtask(epic.getId(), subtask2);

        List<Subtask> subtasks = manager.getAllSubtasksOfEpic(epic.getId());
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtask1.getId() == subtask1.getId());
        assertTrue(subtasks.contains(subtask2));
        assertTrue(subtask2.getId() == subtask2.getId());
    }

    @Test
    void shouldRemoveTaskById() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(1,"Task 1", "Description 1", Duration.ofHours(2), startTime);
        manager.createTask(task);

        List<Task> tasks = manager.getAllTasks();
        System.out.println(tasks);

        boolean removed = manager.deleteTaskById(task.getId());
        assertTrue(removed);

        boolean removedTask = manager.deleteTaskById(task.getId());
        assertFalse(removedTask);
    }

    @Test
    void shouldRemoveEpicById() {
        Epic epic = new Epic(1001,"Epic 1", "Description 1");
        manager.createEpic(epic);

        boolean removed = manager.deleteEpicById(epic.getId());
        assertTrue(removed);

        boolean removedEpic = manager.deleteEpicById(epic.getId());
        assertFalse(removedEpic);
    }

    @Test
    void shouldRemoveSubtaskById() {
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic(1001,"Test Epic", "Test Epic description");
        Subtask subtask = new Subtask(101, "Test Subtask", "Test description", epic.getId(), Duration.ofHours(2), startTime);
        manager.createEpic(epic);
        manager.createSubtask(epic.getId(), subtask);

        boolean removed = manager.deleteSubtaskById(subtask.getId());
        assertTrue(removed);

        assertThrows(NotFoundException.class, () -> manager.getSubtaskById(subtask.getId()),
                "При попытке получить удалённую подзадачу должно выбрасываться исключение NotFoundException.");

        assertThrows(NotFoundException.class, () -> manager.deleteSubtaskById(subtask.getId()),
                "При попытке удалить несуществующую подзадачу должно выбрасываться исключение NotFoundException.");
    }
}