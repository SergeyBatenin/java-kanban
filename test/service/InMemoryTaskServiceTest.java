package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskServiceTest {
    private TaskService taskService;

    @BeforeEach
    void initializeTaskService() {
        taskService = new InMemoryTaskService(new InMemoryHistoryService());
    }

    @Test
    @DisplayName("Создание новой задачи")
    void shouldCreateNewTask() {
        Task task = new Task("name", "description", TaskStatus.NEW);
        final long taskId = taskService.createSimpleTask(task).getId();

        final Task savedTask = taskService.getSimpleTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Создание новой подзадачи")
    void shouldCreateNewSubTask() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.NEW, epicId);
        final long taskId = taskService.createSubTask(task).getId();

        final SubTask savedTask = taskService.getSubTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Создание нового эпика")
    void shouldCreateNewEpic() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        long epicId = taskService.createEpicTask(epic).getId();

        final Epic savedTask = taskService.getEpicTaskById(epicId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление задачи")
    void shouldBeEqualsTaskAfterUpdate() {
        Task task = new Task("name", "description", TaskStatus.NEW);
        final long taskId = taskService.createSimpleTask(task).getId();

        task.setDescription("NEW description");
        taskService.updateSimpleTask(task);
        final Task savedTask = taskService.getSimpleTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление подзадачи")
    void shouldBeEqualsSubTaskAfterUpdate() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.NEW, epicId);
        final long taskId = taskService.createSubTask(task).getId();

        task.setDescription("NEW description");
        taskService.updateSubTask(task);

        final SubTask savedTask = taskService.getSubTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление эпика")
    void shouldBeEqualsEpicAfterUpdateField() {
        Epic task = new Epic("name", "description", TaskStatus.NEW);
        final long taskId = taskService.createEpicTask(task).getId();

        task.setDescription("NEW description");
        taskService.updateEpicTask(task);
        final Epic savedTask = taskService.getEpicTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление статуса эпика на \"В процессе\" после изменения подзадачи")
    void shouldBeEqualsEpicStatusIsProgressAfterUpdateSubtask() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        final long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.NEW, epicId);
        taskService.createSubTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateSubTask(task);

        final Epic savedTask = taskService.getEpicTaskById(epicId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(TaskStatus.IN_PROGRESS, savedTask.getStatus(), "Статусы не совпадают.");
    }

    @Test
    @DisplayName("Обновление статуса эпика на \"Выполнена\" после изменения подзадачи")
    void shouldBeEqualsEpicStatusIsDoneAfterUpdateSubtask() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        final long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.NEW, epicId);
        taskService.createSubTask(task);
        task.setStatus(TaskStatus.DONE);
        taskService.updateSubTask(task);

        final Epic savedTask = taskService.getEpicTaskById(epicId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(TaskStatus.DONE, savedTask.getStatus(), "Статусы не совпадают.");
    }

    @Test
    @DisplayName("Обновление статуса эпика на \"Новый\" после удаления всех подзадач")
    void shouldBeEqualsEpicStatusIsNewAfterUpdateSubtask() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        final long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.IN_PROGRESS, epicId);
        taskService.createSubTask(task);

        final Epic savedTask = taskService.getEpicTaskById(epicId);
        taskService.removeAllSubTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статусы не совпадают.");
    }
}