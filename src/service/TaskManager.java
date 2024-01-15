package service;

import model.*;

import java.util.*;

public class TaskManager {
    private static long taskIdentifier = 0;
    private final Map<Long, Task> simpleTasks;
    private final Map<Long, SubTask> subTasks;
    private final Map<Long, Epic> epicTasks;

    public TaskManager() {
        simpleTasks = new HashMap<>();
        subTasks = new HashMap<>();
        epicTasks = new HashMap<>();
    }

    public Task createSimpleTask(Task task) {
        task.setId(++taskIdentifier);
        simpleTasks.put(task.getId(), task);
        return task;
    }
    public SubTask createSubTask(SubTask task) {
        task.setId(++taskIdentifier);

        long parentId = task.getEpicId();
        Epic taskParent = epicTasks.get(parentId);
        if (taskParent == null) {
            throw new RuntimeException("Подзадача не может существовать без эпика");
        }

        taskParent.getSubTasks().add(task.getId());
        subTasks.put(task.getId(), task);

        return task;
    }
    public Epic createEpicTask(Epic task) {
        task.setId(++taskIdentifier);
        epicTasks.put(task.getId(), task);

        return task;
    }

    public Task updateSimpleTask(Task task) {
        simpleTasks.put(task.getId(), task);
        return task;
    }
    public SubTask updateSubTask(SubTask task) {
        updateEpicStatus(task.getEpicId());
        subTasks.put(task.getId(), task);
        return task;
    }
    public Epic updateEpicTask(Epic task) {
        updateEpicStatus(task.getId());
        epicTasks.put(task.getId(), task);
        return task;
    }

    private void updateEpicStatus(long epicId) {
        Epic epic = epicTasks.get(epicId);
        List<Long> epicSubTasks = epic.getSubTasks();
        if(epicSubTasks.isEmpty()) {
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

    public List<Task> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }
    public List<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }
    public List<SubTask> getAllSubTasksByEpic(Epic epic) {
        List<SubTask> subTaskList = new ArrayList<>();
        List<Long> epicSubTasks = epic.getSubTasks();
        for (Long subtaskId : epicSubTasks) {
            subTaskList.add(subTasks.get(subtaskId));
        }
        return subTaskList;
    }

    public void removeAllSimpleTasks() {
        simpleTasks.clear();
    }
    public void removeAllSubTasks() {
        Collection<SubTask> subtasks = subTasks.values();
        for (SubTask task : subtasks) {
            long parentId = task.getEpicId();
            Epic subTaskParent = epicTasks.get(parentId);
            List<Long> parentSubtasks = subTaskParent.getSubTasks();
            parentSubtasks.remove(task.getId());
            updateEpicStatus(parentId);
        }

        subTasks.clear();
    }
    public void removeAllEpicTasks() {
        subTasks.clear();
        epicTasks.clear();
    }

    public Task getSimpleTaskById(long id) {
        return simpleTasks.get(id);
    }
    public SubTask getSubTaskById(long id) {
        return subTasks.get(id);
    }
    public Epic getEpicTaskById(long id) {
        return epicTasks.get(id);
    }

    public void removeSimpleTaskById(long id) {
        simpleTasks.remove(id);
    }
    public void removeSubTaskById(long id) {
        SubTask subtask = subTasks.get(id);
        long parentId = subtask.getEpicId();
        Epic parent = epicTasks.get(parentId);
        List<Long> parentSubTasks = parent.getSubTasks();
        parentSubTasks.remove(id);
        subTasks.remove(id);
        updateEpicStatus(parentId);
    }
    public void removeEpicTaskById(long id) {
        Epic epic = epicTasks.get(id);
        List<Long> epicSubTasks = epic.getSubTasks();
        for (long subtaskId : epicSubTasks) {
            subTasks.remove(subtaskId);
        }
        epicTasks.remove(id);
    }
}
