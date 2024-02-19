package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryService implements HistoryService {
    private final CustomLinkedList history;
    private final Map<Long, Node> taskCache;

    public InMemoryHistoryService() {
        this.history = new CustomLinkedList();
        this.taskCache = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        final long taskId = task.getId();
        if (taskCache.containsKey(taskId)) {
            remove(taskId);
        }

        final Node node = history.add(task);
        taskCache.put(taskId, node);
    }

    @Override
    public void remove(long id) {
        final Node removedNode = taskCache.remove(id);
        if (removedNode == null) {
            return;
        }

        history.removeNode(removedNode);
    }

    @Override
    public List<Task> getHistory() {
        final List<Task> resultHistory = history.generateHistory();
        return resultHistory;
    }

    private static class CustomLinkedList {
        private Node head;
        private Node tail;
        private int size;

        public CustomLinkedList() {
            head = null;
            tail = null;
            size = 0;
        }

        Node add(Task task) {
            final Node node = new Node(null, task, null);
            if (head == null) {
                head = node;
            } else {
                Node lastNode = tail;
                node.prev = lastNode;
                lastNode.next = node;
            }
            tail = node;
            size++;
            return node;
        }

        void removeNode(Node removedNode) {
            final Node prevNode = removedNode.prev;
            final Node nextNode = removedNode.next;
            if (prevNode == null) {
                head = nextNode;
            } else {
                prevNode.next = nextNode;
                removedNode.prev = null;
            }

            if (nextNode == null) {
                tail = prevNode;
            } else {
                nextNode.prev = prevNode;
                removedNode.next = null;
            }
            size--;
        }

        List<Task> generateHistory() {
            List<Task> historyList = new ArrayList<>();
            Node currentNode = head;
            while (currentNode != null) {
                final Task task = currentNode.task;
                historyList.add(task);
                currentNode = currentNode.next;
            }
            return historyList;
        }
    }
}
