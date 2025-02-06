package com.yandex.app.API;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.yandex.app.HttpTaskServer;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.exception.NotFoundException;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TaskType;

import java.io.IOException;
import java.util.List;

public abstract class BaseTaskHandler extends BaseHttpHandler implements HttpHandler {

    protected final TaskManager taskManager;
    protected final Gson gson = HttpTaskServer.getGson();

    public BaseTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method) && path.matches(".*/\\d+$")) {
                handleGetTaskById(exchange);
            } else if ("GET".equals(method)) {
                handleGetAllTasks(exchange);
            } else if ("POST".equals(method) && path.startsWith("/subtasks")) {
                handleCreateOrUpdate(exchange);
            } else if ("POST".equals(method)) {
                handleCreateOrUpdate(exchange);
            } else if ("DELETE".equals(method) && path.matches(".*/\\d+$")) {
                handleDeleteTaskById(exchange);
            } else if ("DELETE".equals(method)) {
                handleDeleteAllTasks(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    protected abstract Class<? extends Task> getTaskClass();

    protected abstract List<? extends Task> getAllTasks();

    protected abstract int createTask(Task task);

    protected abstract void updateTask(int id, Task task);

    protected abstract Task getTaskById(int id);

    protected abstract void deleteTaskById(int id);

    protected abstract void deleteAllTasks();

    protected void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<? extends Task> tasks = getAllTasks();
        sendText(exchange, gson.toJson(tasks), 200);
    }

    protected void handleCreateOrUpdate(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            System.out.println("Получен JSON: " + body);

            Task task = parseTask(body);
            System.out.println("Распознанная задача: " + task);

            if (task == null) {
                sendError(exchange, "Ошибка: некорректные данные задачи");
                return;
            }

            if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                if (subtask.getEpicId() == null || taskManager.getEpicById(subtask.getEpicId()) == null) {
                    sendError(exchange, "Ошибка: Epic с ID " + subtask.getEpicId() + " не найден");
                    return;
                }
            }
            int id;
            if (task.getId() == null || task.getId() == 0) {
                id = createTask(task);
                System.out.println("Задача создана с ID: " + id);
                sendText(exchange, "{\"id\": " + id + "}", 201);
            } else {
                updateTask(task.getId(), task);
                System.out.println("Задача обновлена");
                sendText(exchange, "{\"message\": \"Task updated successfully\"}", 200);
            }
        } catch (Exception e) {
            System.out.println("Ошибка в handleCreateOrUpdate: " + e.getMessage());
            e.printStackTrace();
            sendError(exchange, e.getMessage());
        }
    }

    protected void handleGetTaskById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        System.out.println("Запрошена задача с ID: " + id);

        Task task = findTaskById(id);
        if (task == null) {
            sendNotFound(exchange, "Задача с ID " + id + " не найдена");
            return;
        }
        sendText(exchange, gson.toJson(task), 200);
    }

    protected void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        System.out.println("🗑 Запрос на удаление задачи с ID: " + id);

        Task task = findTaskById(id);
        if (task == null) {
            sendNotFound(exchange, "Задача с ID " + id + " не найдена");
            return;
        }

        if (task instanceof Subtask) {
            System.out.println("Удаляем подзадачу");
            taskManager.deleteSubtaskById(id);
        } else {
            System.out.println("Удаляем обычную задачу");
            taskManager.deleteTaskById(id);
        }

        sendText(exchange, "", 200);
    }

    protected void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllTasks();
            sendText(exchange, "{\"message\": \"All tasks deleted successfully\"}", 200);
        } catch (Exception e) {
            System.out.println("Ошибка при удалении всех задач: " + e.getMessage());
            sendError(exchange, e.getMessage());
        }
    }

    protected int extractId(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }

    private Task parseTask(String json) {
        Task parsedTask = gson.fromJson(json, Task.class);
        if (parsedTask.getType() == TaskType.SUBTASK) {
            return gson.fromJson(json, Subtask.class);
        }
        return parsedTask;
    }

    protected Task findTaskById(int id) {
        Task task = taskManager.getTaskById(id);
        if (task == null) {
            task = taskManager.getSubtaskById(id);
        }
        if (task == null) {
            task = taskManager.getEpicById(id);
        }
        return task;
    }
}
