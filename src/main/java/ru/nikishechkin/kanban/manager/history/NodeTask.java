package ru.nikishechkin.kanban.manager.history;

import ru.nikishechkin.kanban.model.Task;

class NodeTask {
    public Task data;
    public NodeTask next;
    public NodeTask prev;

    public NodeTask(Task data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
