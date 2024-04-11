package handler;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskService;

import java.io.IOException;
import java.util.List;

import static java.net.HttpURLConnection.*;

public class PrioritizedHandler extends BaseHandler {
    public PrioritizedHandler(TaskService taskService) {
        super(taskService);
    }

    @Override
    void generateAnsSendResponse(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if ("GET".equals(method)) {
            String[] path = httpExchange.getRequestURI().getPath().split("/");

            if (path.length == 2) {
                List<Task> prioritizedTasks = taskService.getPrioritizedTasks();
                sendResponse(httpExchange, HTTP_OK, gson.toJson(prioritizedTasks));
            }
        } else {
            sendResponse(httpExchange, HTTP_BAD_METHOD, gson.toJson(new RequestError("method not supported")));
        }
        sendResponse(httpExchange, HTTP_NOT_FOUND, gson.toJson(new RequestError("Invalid URL")));
    }
}