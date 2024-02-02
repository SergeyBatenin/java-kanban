package service;

public class TaskServiceFactory {
    private static HistoryService historyService = new InMemoryHistoryService();
    private static TaskService taskService = new InMemoryTaskService(historyService);

    public static TaskService getDefault() {
        return taskService;
    }
}
