package com.yandex.app.service;

import com.yandex.app.model.Task;

public class Node {
    Task task;
    Node prev;
    Node next;

    public Node(Task task) {
        this.task = task;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
