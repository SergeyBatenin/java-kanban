package service;

import java.io.File;

public class ServiceFactory {
    public static TaskService getDefaultTaskService() {
        return new InMemoryTaskService();
    }

    public static TaskService getDefaultFileTaskService(File file) {
        return new FileBackedTaskService(file);
    }

    public static HistoryService getDefaultHistoryService() {
        return new InMemoryHistoryService();
    }
}
