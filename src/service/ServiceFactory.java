package service;

public class ServiceFactory {
    private static HistoryService historyService = new InMemoryHistoryService();
    private static TaskService taskService = new InMemoryTaskService(historyService);

    public static TaskService getDefaultTaskService() {
        return taskService;
    }
    public static HistoryService getDefaultHistoryService() {
        return historyService;
    }
}