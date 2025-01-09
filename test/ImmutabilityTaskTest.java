import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImmutabilityTaskTest {
    private TaskManager manager;
    private Task task;

    @BeforeEach
    public void setup() {
        manager = Managers.getDefault();
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        task = new Task(1,"Test addNewTask", "Test addNewTask description", duration, startTime);
    }

    @Test
    void shouldImmutabilityTask() {
        manager.createTask(task);

        Task extractedIdOfTask = manager.getTaskById(task.getId());
        assertEquals(task, extractedIdOfTask);

        Task extractedTitleOfTask = manager.getTaskByTitle(task.getTitle());
        assertEquals(task, extractedTitleOfTask);

        Task extractedDescriptionOfTask = manager.getTaskByDescription(task.getDescription());
        assertEquals(task, extractedDescriptionOfTask);
    }
}