package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    // метод для просмотра последних задач
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void updateHistory(Task task) {
        if (task != null && !history.contains(task)) {
            for (int i = 0; i < history.size(); i++) {
                Task currTask = history.get(i);
                if (currTask.equals(task)) {
                    history.remove(currTask);
                }
            }
            history.add(task);
        }
    }

    @Override
    public void remove(int id) {
        for (int i = 0; i < history.size(); i++) {
            Task task = history.get(i);
            if (task.getId() == id) {
                history.remove(i);
            }
        }
    }

    public Task getCurrentTask(int id) {
        Task latestTask = null;
        for (int i = history.size() - 1; i >= 0; i--) {
            Task t = history.get(i);
            if (t.getId() == id) {
                latestTask = t;
                break;
            }
        }
        return latestTask;
    }

    public Task getLastSavedTask(int id) {
        return getCurrentTask(id);
    }
}
