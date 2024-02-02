package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryService  implements HistoryManager {
    private final LinkedList<Task> history;

    public InMemoryHistoryService() {
        this.history = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (history.size() == 10) {
            history.pollFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
