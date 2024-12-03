package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> history;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        head = new Node(null);
        tail = new Node(null);

        head.next = tail;
        tail.prev = head;
    }

    // метод для добавления задач в конец списка
    public void linkLast(Node newNode) {
        Node currentTail = tail.prev;
        currentTail.next = newNode;
        newNode.prev = currentTail;
        newNode.next = tail;
        tail.prev = newNode;
    }

    // метод для получения задач из списка
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currTask = head.next;
        while (currTask != tail) {
            tasks.add(currTask.task);
            currTask = currTask.next;
        }
        return tasks;
    }

    // метод для удаления узла
    public void removeNode(Node node) {
        if (node == head || node == tail) {
            throw new IllegalArgumentException("Нельзя удалить пустые узлы");
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev; //
        }
        node.prev = null;
        node.next = null;
    }


    // метод для просмотра последних задач
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void updateHistory(Task task) {
        if (task == null) {
            return;
        }
        int id = task.getId();
        Node oldNode = history.get(id);
        if (oldNode != null) {
            removeNode(oldNode);
        }
        Node newNode = new Node(task);
        linkLast(newNode);
        history.put(id, newNode);
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node != null) {
            removeNode(node);
            history.remove(id);
        }
    }

    @Override
    public Task getCurrentTask(int id) {
        Node node = history.get(id);
        return node != null ? node.task : null;
    }

    @Override
    public Task getLastSavedTask(int id) {
        return getCurrentTask(id);
    }

    @Override
    public void clearHistory() {
        history.clear();
        head.next = tail;
        tail.prev = head;
    }
}
