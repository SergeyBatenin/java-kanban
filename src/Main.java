import model.*;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task simple1 = manager.createSimpleTask(new Task("Simple task 1", "Simple description 1", TaskStatus.NEW));
        Epic epic1 = manager.createEpicTask(new Epic("Epic name 1", "Epic descr 1", TaskStatus.NEW));
        Task simple2 = manager.createSimpleTask(new Task("Simple task 2", "Simple description 2", TaskStatus.NEW));
        Epic epic2 = manager.createEpicTask(new Epic("Epic name 2", "Epic descr 2", TaskStatus.NEW));
        System.out.println("Только созданный эпик");
        System.out.println(epic1);
        SubTask subtask1 = manager.createSubTask(new SubTask("subtask name 1", "subtask description 1", TaskStatus.NEW, epic1.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("subtask name2", "subtask description2", TaskStatus.NEW, epic1.getId()));

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
        Task newSimple = new Task("NEW Simple task", "NEW Simple description", TaskStatus.IN_PROGRESS);
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

    }
}