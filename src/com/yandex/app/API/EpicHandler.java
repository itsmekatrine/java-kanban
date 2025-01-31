package com.yandex.app.API;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.yandex.app.HttpTaskServer;
import com.yandex.app.exception.NotFoundException;
import com.yandex.app.model.Epic;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method) && "/epics".equals(path)) {
                handleGetAllEpics(exchange);
            } else if ("POST".equals(method) && "/epics".equals(path)) {
                handleCreateEpic(exchange);
            } else if ("GET".equals(method) && path.matches("/epics/\\d+")) {
                handleGetEpicById(exchange);
            } else if ("DELETE".equals(method) && path.matches("/epics/\\d+")) {
                handleDeleteEpicById(exchange);
            } else {
                sendNotFound(exchange, "Endpoint not found");
            }
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        String response = gson.toJson(epics);
        sendText(exchange, response, 200);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Epic epic = gson.fromJson(body, Epic.class);

        int id = taskManager.createEpic(epic);
        sendText(exchange, "{\"id\": " + id + "}", 201);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        try {
            int epicId = extractId(exchange.getRequestURI().getPath());
            Epic epic = taskManager.getEpicById(epicId);

            sendText(exchange, gson.toJson(epic), 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        try {
            int id = extractId(exchange.getRequestURI().getPath());
            Epic epic = taskManager.getEpicById(id);

            if (epic == null) {
                sendNotFound(exchange, "Epic with ID " + id + " not found");
                return;
            }

            taskManager.deleteEpicById(id);
            sendText(exchange, "{\"message\": \"Epic deleted successfully\"}", 200);

        } catch (NotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            System.out.println("Внутренняя ошибка сервера: " + e.getMessage());
            sendError(exchange, "Internal server error: " + e.getMessage());
        }
    }

    private int extractId(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }
}
