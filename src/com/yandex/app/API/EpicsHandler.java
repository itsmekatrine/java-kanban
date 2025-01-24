package com.yandex.app.API;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.yandex.app.model.Epic;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public EpicsHandler(TaskManager taskManager) {
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
        sendText(exchange, gson.toJson(epics), 200);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Epic epic = gson.fromJson(body, Epic.class);

        int id = taskManager.createEpic(epic);
        sendText(exchange, "{\"id\": " + id + "}", 201);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        Epic epic = taskManager.getEpicById(id);

        if (epic == null) {
            sendNotFound(exchange, "Epic not found");
        } else {
            sendText(exchange, gson.toJson(epic), 200);
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        boolean deleted = taskManager.deleteEpicById(id);

        if (deleted) {
            sendText(exchange, "", 204);
        } else {
            sendNotFound(exchange, "Epic not found");
        }
    }

    private int extractId(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }
}
