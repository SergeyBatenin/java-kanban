import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.TaskService;
import service.ServiceFactory;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskService manager = ServiceFactory.getDefaultTaskService();
        Task simple1 = manager.createSimpleTask(new Task(
                "Simple task 1",
                "Simple description 1",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 3, 9, 0),
                Duration.ofMinutes(15)));
        Epic epic1 = manager.createEpicTask(new Epic(
                "Epic name 1",
                "Epic descr 1",
                TaskStatus.NEW,
                null,
                Duration.ZERO));
        Task simple2 = manager.createSimpleTask(new Task(
                "Simple task 2",
                "Simple description 2",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 3, 10, 0),
                Duration.ofMinutes(15)));
        Epic epic2 = manager.createEpicTask(new Epic(
                "Epic name 2",
                "Epic descr 2",
                TaskStatus.NEW,
                null,
                Duration.ZERO));
        System.out.println("Только созданный эпик");
        System.out.println(epic1);
        SubTask subtask1 = manager.createSubTask(new SubTask(
                "subtask name 1",
                "subtask description 1",
                TaskStatus.NEW,
                epic1.getId(),
                LocalDateTime.of(2024, 3, 3, 9, 15),
                Duration.ofMinutes(15)));
        SubTask subtask2 = manager.createSubTask(new SubTask(
                "subtask name2",
                "subtask description2",
                TaskStatus.NEW,
                epic1.getId(),
                LocalDateTime.of(2024, 3, 3, 9, 30),
                Duration.ofMinutes(15)));

        System.out.println("Эпик с добавленными в него подзадачами");
        System.out.println(epic1);
        System.out.println();

        System.out.println("Списки всех задач");
        System.out.println(manager.getAllSimpleTasks());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getAllEpicTasks());
        System.out.println();

        System.out.println("Задачи после удаления по айди");
        manager.removeSimpleTaskById(1);
        System.out.println(manager.getAllSimpleTasks());
        System.out.println();

        System.out.println("Список простых задач после изменения задачи");
        Task newSimple = new Task(
                "NEW Simple task",
                "NEW Simple description",
                TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 3, 3, 10, 0),
                Duration.ofMinutes(15));
        newSimple.setId(simple2.getId());
        manager.updateSimpleTask(newSimple);
        System.out.println(manager.getAllSimpleTasks());
        System.out.println();

        System.out.println("Список подзадач эпика");
        System.out.println(manager.getAllSubTasksByEpic(epic1));
        System.out.println();

        System.out.println("Список подзадач эпика после изменения статуса одной из подзадач");
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subtask1);
        System.out.println(manager.getAllSubTasksByEpic(epic1));
        System.out.println(epic1);
        System.out.println();

        System.out.println("Список подзадач эпика после изменения статусов всех подзадач на DONE");
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subtask1);
        manager.updateSubTask(subtask2);
        System.out.println(manager.getAllSubTasksByEpic(epic1));
        System.out.println(epic1);
        System.out.println();

        System.out.println("Списки подзадач и эпиков после удаления подзадачи");
        manager.removeSubTaskById(5);
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getAllEpicTasks());
        System.out.println();

        System.out.println("Списки подзадач и эпиков после удаления эпика");
        manager.removeEpicTaskById(2);
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getAllEpicTasks());
        System.out.println();

        System.out.println("Смотрим историю когда она пустая");
        System.out.println(manager.getHistory());
        System.out.println();

        System.out.println("Смотрим историю когда смотрели несколько задач");
        manager.getSimpleTaskById(3); // 1 просмотр
        manager.getEpicTaskById(4); // 2 просмотр
        System.out.println(manager.getHistory());
        System.out.println();

        Epic epic3 = manager.createEpicTask(new Epic(
                "Epic name 3",
                "Epic descr 3",
                TaskStatus.NEW,
                null,
                Duration.ofMinutes(15)));
        SubTask subtask3 = manager.createSubTask(new SubTask(
                "subtask name 1",
                "subtask description 1",
                TaskStatus.NEW,
                epic3.getId(),
                LocalDateTime.of(2024, 3, 3, 9, 0),
                Duration.ofMinutes(15)));
        SubTask subtask4 = manager.createSubTask(new SubTask(
                "subtask name2",
                "subtask description2",
                TaskStatus.NEW,
                epic3.getId(),
                LocalDateTime.of(2024, 3, 3, 9, 0),
                Duration.ofMinutes(15)));
        Epic epic4 = manager.createEpicTask(new Epic(
                "Epic name 3",
                "Epic descr 3",
                TaskStatus.NEW,
                null,
                Duration.ofMinutes(15)));

        System.out.println("Смотрим историю после того как просмотрели некоторые задачи повторно");
        manager.getSimpleTaskById(3); // 3 просмотр
        manager.getEpicTaskById(4); // 4 просмотр
        manager.getSubTaskById(subtask4.getId()); // 5 просмотр
        manager.getSubTaskById(subtask3.getId()); // 6 просмотр
        manager.getEpicTaskById(epic3.getId()); // 7 просмотр
        manager.getSimpleTaskById(3); // 8 просмотр (повторный)
        manager.getEpicTaskById(4); // 9 просмотр(повторный)
        manager.getEpicTaskById(epic4.getId()); // 10 просмотр
        System.out.println(manager.getHistory());
        System.out.println();

        System.out.println("Смотрим историю после удаления эпика без подзадач");
        manager.removeEpicTaskById(epic4.getId());
        System.out.println(manager.getHistory());
        System.out.println();

        System.out.println("Смотрим историю после удаления всех эпиков");
        manager.removeAllEpicTasks();
        System.out.println(manager.getHistory());
        System.out.println();
    }
}