package com.yandex.app.API;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.yandex.app.HttpTaskServer;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod()) && "/prioritized".equals(exchange.getRequestURI().getPath())) {
                handleGetPrioritizedTasks(exchange);
            } else {
                sendNotFound(exchange, "Endpoint not found");
            }
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        try {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            if (prioritizedTasks == null) {
                prioritizedTasks = new ArrayList<>();
            }

            String response = gson.toJson(prioritizedTasks);
            sendText(exchange, response, 200);

        } catch (Exception e) {
            System.out.println("Ошибка при получении приоритетных задач: " + e.getMessage());
            sendError(exchange, "Internal server error: " + e.getMessage());
        }
    }
}
