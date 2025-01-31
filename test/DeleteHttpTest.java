import com.yandex.app.exception.NotFoundException;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteHttpTest extends BaseHttpTest {

    @Test
    public void shouldDeleteAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description 2", Duration.ofMinutes(20), LocalDateTime.now().plusHours(2));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пустым после удаления всех задач.");
    }

    @Test
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        int taskId = taskManager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(taskManager.getTaskById(taskId));
    }

    @Test
    public void shouldReturn404ForNonexistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1001,"Epic 1", "Description 1");
        int epicId = taskManager.createEpic(epic1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        try {
            assertNull(taskManager.getEpicById(epicId));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void shouldReturn404ForNonexistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/99"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(1001, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(101, "Subtask 1", "Description 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        int subtaskId = taskManager.createSubtask(epic.getId(), subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        try {
            assertNull(taskManager.getSubtaskById(subtaskId));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void shouldReturn404ForNonexistentSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/99"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}