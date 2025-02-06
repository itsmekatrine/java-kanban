package com.yandex.app.API;

import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;

import java.util.List;

public class TaskHandler extends BaseTaskHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected Class<? extends Task> getTaskClass() {
        return Task.class;
    }

    @Override
    protected int createTask(Task task) {
        return taskManager.createTask(task);
    }

    @Override
    protected void updateTask(int id, Task task) {
        taskManager.updateTask(id, task);
    }

    @Override
    protected List<Task> getAllTasks() {
        return taskManager.getAllTasks();
    }

    @Override
    protected Task getTaskById(int id) {
        return taskManager.getTaskById(id);
    }

    @Override
    protected void deleteTaskById(int id) {
        taskManager.deleteTaskById(id);
    }

    @Override
    protected void deleteAllTasks() {
        taskManager.deleteAllTasks();
    }
}