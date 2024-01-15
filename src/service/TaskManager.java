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
    public SubTask createSubTask(Task task) {
        SubTask subTask = new SubTask();
        subTask.setId(++taskIdentifier);
        subTask.setName(task.getName());
        subTask.setDescription(task.getDescription());
        subTask.setStatus(TaskStatus.NEW);
        if (task instanceof SubTask) {
            long parentId = ((SubTask) task).getEpicId();
            subTask.setEpicId(parentId);
            epicTasks.get(parentId).getSubTasks().add(subTask);
        } else {
            throw new RuntimeException("Подзадача не может существовать без эпика");
        }

        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }
    public Epic createEpicTask(Task task) {
        Epic epicTask = new Epic();
        epicTask.setId(++taskIdentifier);
        epicTask.setName(task.getName());
        epicTask.setDescription(task.getDescription());
        epicTask.setStatus(TaskStatus.NEW);

        epicTasks.put(epicTask.getId(), epicTask);
        return epicTask;
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
        List<SubTask> epicSubTasks = epic.getSubTasks();
        if(epicSubTasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            int countTaskWithNewStatus = 0;
            for (SubTask task : epicSubTasks) {
                TaskStatus status = task.getStatus();
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
        return epic.getSubTasks();
    }

    public void removeAllSimpleTasks() {
        simpleTasks.clear();
    }
    public void removeAllSubTasks() {
        Collection<SubTask> subtasks = subTasks.values();
        for (SubTask task : subtasks) {
            long parentId = task.getEpicId();
            Epic subTaskParent = epicTasks.get(parentId);
            List<SubTask> parentSubtasks = subTaskParent.getSubTasks();
            parentSubtasks.remove(task);
            updateEpicStatus(parentId);
        }

        subTasks.clear();
    }
    public void removeAllEpicTasks() {
        Collection<Epic> epics = epicTasks.values();
        for (Epic epic : epics) {
            List<SubTask> subTasksEpic = epic.getSubTasks();
            for (SubTask subTaskEpic : subTasksEpic) {
                subTasks.remove(subTaskEpic.getId());
            }
        }
        // Не уверен конечно в целесообразности верхних циклов. По идее, если не будет эпиков, то и подзадач
        // тоже не должно существовать и можно просто очищать мапу с ними
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
        List<SubTask> parentSubTasks = parent.getSubTasks();
        parentSubTasks.remove(subtask);
        subTasks.remove(id);
        updateEpicStatus(parentId);
    }
    public void removeEpicTaskById(long id) {
        Epic epic = epicTasks.get(id);
        List<SubTask> epicSubTasks = epic.getSubTasks();
        for (SubTask epicSubTask : epicSubTasks) {
            subTasks.remove(epicSubTask.getId());
        }
        epicTasks.remove(id);
    }
}
