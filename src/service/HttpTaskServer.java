package service;

import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import exception.TaskServerStartException;
import handler.*;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskService taskService;

    public HttpTaskServer(TaskService taskService) throws IOException {
        this.taskService = taskService;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskService));
        server.createContext("/subtasks", new SubtaskHandler(taskService));
        server.createContext("/epics", new EpicHandler(taskService));
        server.createContext("/history", new HistoryHandler(taskService));
        server.createContext("/prioritized", new PrioritizedHandler(taskService));
    }

    public void startServer() {
        server.start();
    }

    public void stopServer() {
        server.stop(1);
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer taskServer = new HttpTaskServer(
                    ServiceFactory.getDefaultFileTaskService(new File("resources/backup.csv")));
            Task task = new Task(
                    "name",
                    "description",
                    TaskStatus.NEW,
                    LocalDateTime.of(2024, 3, 1, 9, 0),
                    Duration.ofMinutes(15));
            taskServer.taskService.createSimpleTask(task);
            Epic epic = new Epic("name", "description", TaskStatus.NEW, null, Duration.ZERO);
            taskServer.taskService.createEpicTask(epic);
            SubTask subtask = new SubTask(
                    "name",
                    "description",
                    TaskStatus.NEW,
                    epic.getId(),
                    LocalDateTime.of(2024, 3, 2, 9, 0),
                    Duration.ofMinutes(15));
            taskServer.taskService.createSubTask(subtask);
            taskServer.startServer();
//            taskServer.stopServer();
        } catch (IOException e) {
            throw new TaskServerStartException("Ошибка при запуске сервера", e);
        }
    }
}
