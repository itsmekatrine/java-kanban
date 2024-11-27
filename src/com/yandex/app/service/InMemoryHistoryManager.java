package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> map;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        map = new HashMap<>();
    }

    // метод для добавления задач в конец списка
    public void linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    // метод для получения задач из списка
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currTask = head;
        while (currTask != null) {
            tasks.add(currTask.task);
            currTask = currTask.next;
        }
        return tasks;
    }

    // метод для удаления узла
    public void removeNode(Node node) {
        if (node == head) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node == tail) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
    }

    // метод для добавления задач с учётом удаления предыдущих версий
    public void add(Task task) {
        if (task == null ) {
            return;
        }
        int id = task.getId();
        Node oldNode = map.get(id);
        if (oldNode != null) {
            removeNode(oldNode);
        }
        linkLast(task);
        map.put(id, tail);
    }

    // метод для просмотра последних задач
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    // метод для обновления истории
    @Override
    public void updateHistory(Task task) {
        if (task == null ) {
            return;
        }
        int id = task.getId();
        Node oldNode = map.get(id);
        if (oldNode != null) {
            oldNode.task = task;
        }
        linkLast(task);
        map.put(id, tail);
    }

    // метод для удаления задач из истории
    @Override
    public void remove(int id) {
        Node node = map.get(id);
        if (node != null) {
            removeNode(node);
            map.remove(id);
        }
    }

    public Task getCurrentTask(int id) {
        Node node = map.get(id);
        return node != null ? node.task : null;
    }

    public Task getLastSavedTask(int id) {
        return getCurrentTask(id);
    }
}
