package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.util.*;

public class InMemoryTaskService implements TaskService {
    protected static long taskIdentifier = 0;
    protected final Map<Long, Task> simpleTasks;
    protected final Map<Long, SubTask> subTasks;
    protected final Map<Long, Epic> epicTasks;
    protected final HistoryService historyManager;

    public InMemoryTaskService() {
        simpleTasks = new HashMap<>();
        subTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        this.historyManager = ServiceFactory.getDefaultHistoryService();
    }

    @Override
    public Task createSimpleTask(Task task) {
        task.setId(++taskIdentifier);
        simpleTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask task) {
        task.setId(++taskIdentifier);

        long parentId = task.getEpicId();
        Epic taskParent = epicTasks.get(parentId);
        if (taskParent == null) {
            throw new RuntimeException("Подзадача не может существовать без эпика");
        }

        taskParent.getSubTaskIds().add(task.getId());
        subTasks.put(task.getId(), task);
        updateEpicStatus(parentId);

        return task;
    }

    @Override
    public Epic createEpicTask(Epic task) {
        task.setId(++taskIdentifier);
        epicTasks.put(task.getId(), task);

        return task;
    }

    @Override
    public Task updateSimpleTask(Task task) {
        if (simpleTasks.get(task.getId()) == null) {
            throw new RuntimeException("Такой задачи не существует");
        }

        simpleTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        if (subTasks.get(task.getId()) == null) {
            throw new RuntimeException("Такой подзадачи не существует");
        }
        Epic epic = epicTasks.get(task.getEpicId());
        if (epic == null) {
            throw new RuntimeException("Эпика связанного с этой подзадачей не существует");
        }

        subTasks.put(task.getId(), task);
        updateEpicStatus(task.getEpicId());
        return task;
    }

    @Override
    public Epic updateEpicTask(Epic task) {
        Epic saved = epicTasks.get(task.getId());
        if (saved == null) {
            throw new RuntimeException("Такого эпика не существует");
        }

        saved.setName(task.getName());
        saved.setDescription(task.getDescription());
        epicTasks.put(saved.getId(), saved);

        return task;
    }

    private void updateEpicStatus(long epicId) {
        Epic epic = epicTasks.get(epicId);
        List<Long> epicSubTasks = epic.getSubTaskIds();
        if (epicSubTasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            int countTaskWithNewStatus = 0;
            for (long taskId : epicSubTasks) {
                TaskStatus status = subTasks.get(taskId).getStatus();
                if (status == TaskStatus.IN_PROGRESS) {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                    return;
                } else if (status == TaskStatus.NEW) {
                    countTaskWithNewStatus++;
                }
            }
            if (countTaskWithNewStatus == 0) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.NEW);
            }
        }
    }

    @Override
    public List<Task> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<SubTask> getAllSubTasksByEpic(Epic epic) {
        List<SubTask> subTaskList = new ArrayList<>();
        List<Long> epicSubTasks = epic.getSubTaskIds();
        for (Long subtaskId : epicSubTasks) {
            subTaskList.add(subTasks.get(subtaskId));
        }
        return subTaskList;
    }

    @Override
    public void removeAllSimpleTasks() {
        simpleTasks.values().forEach(task -> historyManager.remove(task.getId()));
        simpleTasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        Collection<Epic> epics = epicTasks.values();
        for (Epic epic : epics) {
            epic.getSubTaskIds().clear();
            updateEpicStatus(epic.getId());
        }
        subTasks.values().forEach(task -> historyManager.remove(task.getId()));
        subTasks.clear();
    }

    @Override
    public void removeAllEpicTasks() {
        subTasks.values().forEach(task -> historyManager.remove(task.getId()));
        subTasks.clear();
        epicTasks.values().forEach(task -> historyManager.remove(task.getId()));
        epicTasks.clear();
    }

    @Override
    public Task getSimpleTaskById(long id) {
        final Task task = simpleTasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTaskById(long id) {
        final SubTask subtask = subTasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicTaskById(long id) {
        final Epic epic = epicTasks.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void removeSimpleTaskById(long id) {
        Task removedTask = simpleTasks.remove(id);
        if (removedTask == null) {
            throw new RuntimeException("Задачи с айди {" + id + "} не существует");
        }
        historyManager.remove(id);
    }

    @Override
    public void removeSubTaskById(long id) {
        SubTask removedSubtask = subTasks.remove(id);
        if (removedSubtask == null) {
            throw new RuntimeException("Подзадачи с айди {" + id + "} не существует");
        }
        historyManager.remove(id);

        long parentId = removedSubtask.getEpicId();
        Epic parent = epicTasks.get(parentId);
        if (parent == null) {
            throw new RuntimeException("Эпика связанного с этой подзадачей не существует");
        }

        parent.getSubTaskIds().remove(id);
        updateEpicStatus(parentId);
    }

    @Override
    public void removeEpicTaskById(long id) {
        Epic removedEpic = epicTasks.remove(id);
        if (removedEpic == null) {
            throw new RuntimeException("Эпика с айди {" + id + "} не существует");
        }
        historyManager.remove(id);

        List<Long> epicSubTasks = removedEpic.getSubTaskIds();
        for (long subTaskId : epicSubTasks) {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
