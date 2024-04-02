package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskService {
    Task createSimpleTask(Task task);

    SubTask createSubTask(SubTask task);

    Epic createEpicTask(Epic task);

    Task updateSimpleTask(Task task);

    SubTask updateSubTask(SubTask task);

    Epic updateEpicTask(Epic task);

    List<Task> getAllSimpleTasks();

    List<SubTask> getAllSubTasks();

    List<Epic> getAllEpicTasks();

    List<SubTask> getAllSubTasksByEpic(Epic epic);

    void removeAllSimpleTasks();

    void removeAllSubTasks();

    void removeAllEpicTasks();

    Task getSimpleTaskById(long id);

    SubTask getSubTaskById(long id);

    Epic getEpicTaskById(long id);

    void removeSimpleTaskById(long id);

    void removeSubTaskById(long id);

    void removeEpicTaskById(long id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
