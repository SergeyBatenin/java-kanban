package service;

public class ServiceFactory {
    private static final HistoryService historyService = new InMemoryHistoryService();
    private static final TaskService taskService = new InMemoryTaskService();

    public static TaskService getDefaultTaskService() {
        return taskService;
    }

    public static HistoryService getDefaultHistoryService() {
        return new InMemoryHistoryService();
    }
}
