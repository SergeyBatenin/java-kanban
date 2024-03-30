package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskService implements TaskService {
    protected long taskIdentifier;
    protected final Map<Long, Task> simpleTasks;
    protected final Map<Long, SubTask> subTasks;
    protected final Map<Long, Epic> epicTasks;
    protected final HistoryService historyManager;

    protected final TreeSet<Task> prioritizedTasks;

    public InMemoryTaskService() {
        taskIdentifier = 0;
        simpleTasks = new HashMap<>();
        subTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        this.historyManager = ServiceFactory.getDefaultHistoryService();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime).thenComparing(Task::getId));
    }

    @Override
    public Task createSimpleTask(Task task) {
        task.setId(++taskIdentifier);
        simpleTasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
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
        updateEpicTimeData(parentId);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

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
        long id = task.getId();
        final Task updatedTask = simpleTasks.get(id);
        if (updatedTask == null) {
            throw new RuntimeException("Такой задачи не существует");
        }

        prioritizedTasks.remove(updatedTask);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        simpleTasks.put(id, task);

        return task;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        long id = task.getId();

        final SubTask updatedTask = subTasks.get(id);
        if (updatedTask == null) {
            throw new RuntimeException("Такой подзадачи не существует");
        }

        long epicId = task.getEpicId();
        Epic epic = epicTasks.get(epicId);
        if (epic == null) {
            throw new RuntimeException("Эпика связанного с этой подзадачей не существует");
        }

        prioritizedTasks.remove(updatedTask);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        subTasks.put(id, task);
        updateEpicStatus(epicId);
        updateEpicTimeData(epicId);
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

    private void updateEpicTimeData(long epicId) {
        final Epic epic = epicTasks.get(epicId);
        LocalDateTime epicStart = null;
        LocalDateTime epicEnd = null;
        Duration duration = Duration.ZERO;

        List<SubTask> epicSubTasks = epic.getSubTaskIds().stream()
                .map(subTasks::get)
                .toList();

        LocalDateTime startTime;
        LocalDateTime endTime;
        for (SubTask subTask : epicSubTasks) {
            duration = duration.plus(subTask.getDuration());

            startTime = subTask.getStartTime();
            if (startTime != null) {
                if (epicStart == null || startTime.isBefore(epicStart)) {
                    epicStart = startTime;
                }
                endTime = subTask.getEndTime();
                if (epicEnd == null || endTime.isAfter(epicEnd)) {
                    epicEnd = endTime;
                }
            }
        }
        epic.setStartTime(epicStart);
        epic.setDuration(duration);
        epic.setEndTime(epicEnd);
        epicTasks.put(epic.getId(), epic);
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
            updateEpicTimeData(epic.getId());
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
        updateEpicTimeData(parentId);
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}
