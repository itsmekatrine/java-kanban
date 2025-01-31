import com.google.gson.Gson;
import com.yandex.app.HttpTaskServer;
import com.yandex.app.service.InMemoryHistoryManager;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.io.IOException;
import java.net.http.HttpClient;

public abstract class BaseHttpTest {
    protected HttpTaskServer taskServer;
    protected TaskManager taskManager;
    protected HttpClient client;
    protected Gson gson;

    @BeforeEach
    public void setup() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskServer = new HttpTaskServer(taskManager);
        client = HttpClient.newHttpClient();
        gson = HttpTaskServer.getGson();
        if (taskServer == null) {
            taskServer = new HttpTaskServer(taskManager);
        }
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutdown() {
        taskServer.stop();
    }
}