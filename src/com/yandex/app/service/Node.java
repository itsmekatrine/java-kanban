package com.yandex.app.service;

import com.yandex.app.model.Task;

public class Node {
    Task task;
    Node prev;
    Node next;

    public Node(Task task) {
        this.task = task;
    }
}
