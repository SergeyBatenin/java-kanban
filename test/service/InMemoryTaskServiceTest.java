package service;

import org.junit.jupiter.api.BeforeEach;


class InMemoryTaskServiceTest extends TaskServiceTest<InMemoryTaskService> {

    @Override
    @BeforeEach
    void initializeTaskService() {
        taskService = new InMemoryTaskService();
        super.initializeTasks();
    }
}