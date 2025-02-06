package com.yandex.app.API;

import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;

import java.util.List;

public class SubtaskHandler extends BaseTaskHandler implements HttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected Class<? extends Task> getTaskClass() {
        return Subtask.class;
    }

    @Override
    protected int createTask(Task task) {
        if (!(task instanceof Subtask)) {
            throw new IllegalArgumentException("Ошибка: Task не является Subtask!");
        }
        Subtask subtask = (Subtask) task;
        return taskManager.createSubtask(subtask.getEpicId(), subtask);
    }

    @Override
    protected void updateTask(int id, Task task) {
        taskManager.updateSubtask(id, (Subtask) task);
    }

    @Override
    protected List<Subtask> getAllTasks() {
        return taskManager.getAllSubtasks();
    }

    @Override
    protected Subtask getTaskById(int id) {
        return taskManager.getSubtaskById(id);
    }

    @Override
    protected void deleteTaskById(int id) {
        taskManager.deleteSubtaskById(id);
    }

    @Override
    protected void deleteAllTasks() {
        taskManager.deleteAllSubtasks();
    }
}