import com.google.gson.reflect.TypeToken;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedHttpTest extends BaseHttpTest {

    @Test
    public void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        Task task2 = new Task(2, "Task 2", "Description 2", Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task task3 = new Task(3, "Task 3", "Description 3", Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        System.out.println("Приоритетные задачи перед запросом: " + taskManager.getPrioritizedTasks());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200, но получен " + response.statusCode());

        Type taskListType = new TypeToken<List<Task>>() {}.getType();
        List<Task> tasksFromServer = gson.fromJson(response.body(), taskListType);

        System.out.println("Приоритетные задачи от сервера: " + tasksFromServer);

        assertNotNull(tasksFromServer, "Список приоритетных задач пуст");
        assertEquals(3, tasksFromServer.size(), "Некорректное количество задач");

        assertTrue(tasksFromServer.get(0).getStartTime().isBefore(tasksFromServer.get(1).getStartTime()));
        assertTrue(tasksFromServer.get(1).getStartTime().isBefore(tasksFromServer.get(2).getStartTime()));
    }
}
