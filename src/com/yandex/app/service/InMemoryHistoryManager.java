package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> history;
    private static final int MAX_HISTORY_SIZE = 10;

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
            if (history.size() == MAX_HISTORY_SIZE) {
                history.remove(0);
            }
            history.add(task);
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
