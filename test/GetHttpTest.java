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

public class GetHttpTest extends BaseHttpTest {

    @Test
    public void shouldReturnAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description 2", Duration.ofMinutes(20), LocalDateTime.now().plusHours(2));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task 1"));
        assertTrue(response.body().contains("Task 2"));
    }

    @Test
    public void shouldReturnTaskById() throws IOException, InterruptedException {
        Task task = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        int taskId = taskManager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200, но получен " + response.statusCode());

        Task returnedTask = gson.fromJson(response.body(), Task.class);

        assertNotNull(returnedTask, "Задача не возвращается");
        assertEquals(taskId, returnedTask.getId(), "ID задачи не совпадает");
        assertEquals(task.getTitle(), returnedTask.getTitle(), "Название задачи не совпадает");
        assertEquals(task.getDescription(), returnedTask.getDescription(), "Описание задачи не совпадает");
    }

    @Test
    void shouldReturn404ForNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/9999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404, но получен " + response.statusCode());
    }

    @Test
    public void shouldReturnAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1001,"Epic 1", "Description 1");
        Epic epic2 = new Epic(1002,"Epic 2", "Description 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic 1"));
        assertTrue(response.body().contains("Epic 2"));
    }

    @Test
    public void shouldReturnEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(1001,"Epic 1", "Description 1");
        int epicId = taskManager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200, но получен " + response.statusCode());

        Task returnedEpic = gson.fromJson(response.body(), Task.class);

        assertNotNull(returnedEpic, "Эпик не возвращается");
        assertEquals(epicId, returnedEpic.getId(), "ID эпика не совпадает");
        assertEquals(epic.getTitle(), returnedEpic.getTitle(), "Название эпика не совпадает");
        assertEquals(epic.getDescription(), returnedEpic.getDescription(), "Описание эпика не совпадает");
    }

    @Test
    void shouldReturn404ForNonExistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/9999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404, но получен " + response.statusCode());
    }

    @Test
    public void shouldReturnAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(1001, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(101, "Subtask 1", "Description 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        Subtask subtask2 = new Subtask(102, "Subtask 2", "Description 2", epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        taskManager.createSubtask(epic.getId(), subtask1);
        taskManager.createSubtask(epic.getId(), subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask 1"));
        assertTrue(response.body().contains("Subtask 2"));
    }

    @Test
    public void shouldReturnSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(1001, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(101, "Subtask 1", "Description 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        int subtaskId = taskManager.createSubtask(epic.getId(), subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200, но получен " + response.statusCode());

        Task returnedSubtask = gson.fromJson(response.body(), Task.class);

        assertNotNull(returnedSubtask, "Подзадача не возвращается");
        assertEquals(subtaskId, returnedSubtask.getId(), "ID подзадачи не совпадает");
        assertEquals(subtask.getTitle(), returnedSubtask.getTitle(), "Название подзадачи не совпадает");
        assertEquals(subtask.getDescription(), returnedSubtask.getDescription(), "Описание подзадачи не совпадает");
    }

    @Test
    void shouldReturn404ForNonExistentSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/9999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404, но получен " + response.statusCode());
    }
}