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

public class HistoryHttpTest extends BaseHttpTest {

    @Test
    public void shouldReturnTaskHistory() throws IOException, InterruptedException {
        Task task = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        int taskId = taskManager.createTask(task);
        taskManager.getTaskById(taskId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task 1"));
    }

    @Test
    public void shouldReturnEpicHistory() throws IOException, InterruptedException {
        Epic epic = new Epic(1001,"Epic 1", "Description 1");
        int epicId = taskManager.createEpic(epic);
        taskManager.getEpicById(epicId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic 1"));
    }

    @Test
    public void shouldReturnSubtaskHistory() throws IOException, InterruptedException {
        Epic epic = new Epic(1001, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(101, "Subtask 1", "Description 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        int subtaskId = taskManager.createSubtask(epic.getId(), subtask);
        taskManager.getSubtaskById(subtaskId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask 1"));
    }
}