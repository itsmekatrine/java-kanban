package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>(10);
    }

    // метод для просмотра последних задач
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void updateHistory(Task task) {
        if (!history.contains(task)) {
            if (history.size() == 10) {
                history.removeFirst();
            }
            history.addLast(task);
        }
    }

    @Override
    public void add(Task task) {
        history.add(task);
    }
}
