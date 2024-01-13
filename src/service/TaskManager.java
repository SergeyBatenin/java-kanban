package service;

import model.*;

import java.util.*;

public class TaskManager {
    private static long taskIdentifier = 0;
    private final Map<Long, SimpleTask> simpleTasks;
    private final Map<Long, SubTask> subTasks;
    private final Map<Long, Epic> epicTasks;

    public TaskManager() {
        simpleTasks = new HashMap<>();
        subTasks = new HashMap<>();
        epicTasks = new HashMap<>();
    }

    public SimpleTask createSimpleTask(Task task) {
        SimpleTask simpleTask = new SimpleTask();
        simpleTask.setId(++taskIdentifier);
        simpleTask.setName(task.getName());
        simpleTask.setDescription(task.getDescription());
        simpleTask.setStatus(TaskStatus.NEW);

        simpleTasks.put(simpleTask.getId(), simpleTask);
        return simpleTask;
    }
    public SubTask createSubTask(Task task) {
        SubTask subTask = new SubTask();
        subTask.setId(++taskIdentifier);
        subTask.setName(task.getName());
        subTask.setDescription(task.getDescription());
        subTask.setStatus(TaskStatus.NEW);
        if (task instanceof SubTask) {
            Epic parent = ((SubTask) task).getEpic();
            subTask.setEpic(parent);
            parent.getSubTasks().add(subTask);
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

    public SimpleTask updateSimpleTask(SimpleTask task) {
        simpleTasks.put(task.getId(), task);
        return task;
    }
    public SubTask updateSubTask(SubTask task) {
        checkEpicStatus(task.getEpic());
        subTasks.put(task.getId(), task);
        return task;
    }
    public Epic updateEpicTask(Epic task) {
        checkEpicStatus(task);
        epicTasks.put(task.getId(), task);
        return task;
    }

    private void checkEpicStatus(Epic epic) {
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

    public List<SimpleTask> getAllSimpleTasks() {
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
            Epic subTaskParent = task.getEpic();
            List<SubTask> parentSubtasks = subTaskParent.getSubTasks();
            parentSubtasks.remove(task);
            checkEpicStatus(subTaskParent);
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

    public SimpleTask getSimpleTaskById(long id) {
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
        Epic parent = subtask.getEpic();
        List<SubTask> parentSubTasks = parent.getSubTasks();
        parentSubTasks.remove(subtask);
        subTasks.remove(id);
        checkEpicStatus(parent);
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
