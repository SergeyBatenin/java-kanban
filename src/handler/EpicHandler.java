package handler;

import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import service.TaskService;

import java.io.IOException;
import java.util.List;

import static java.net.HttpURLConnection.*;

public class EpicHandler extends BaseHandler {
    public EpicHandler(TaskService taskService) {
        super(taskService);
    }

    @Override
    void generateAnsSendResponse(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                getHandler(httpExchange);
                break;
            case "POST":
                postHandler(httpExchange);
                break;
            case "DELETE":
                deleteHandler(httpExchange);
                break;
            default:
                // 405 - method not supported
                sendResponse(httpExchange, HTTP_BAD_METHOD, gson.toJson(new RequestError("method not supported")));
        }
    }

    private void getHandler(HttpExchange httpExchange) throws IOException {
        String[] path = httpExchange.getRequestURI().getPath().split("/");

        if (path.length == 2) {
            List<Epic> tasks = taskService.getAllEpicTasks();
            sendResponse(httpExchange, HTTP_OK, gson.toJson(tasks));
            return;
        }

        if (path.length == 3) {
            // 200 - есть задача, 400 - невалидный id, 404 - задачи не существует
            long id = getIdFromRequest(path[2]);
            Epic task = taskService.getEpicTaskById(id);
            if (task == null) {
                sendResponse(httpExchange, HTTP_NOT_FOUND, gson.toJson(new RequestError("Task not found")));
            } else {
                sendResponse(httpExchange, HTTP_OK, gson.toJson(task));
            }
            return;
        }
        // 404 - некорректным урлом
        sendResponse(httpExchange, HTTP_NOT_FOUND, gson.toJson(new RequestError("Invalid URL")));
    }

    private void postHandler(HttpExchange httpExchange) throws IOException {
        String[] path = httpExchange.getRequestURI().getPath().split("/");
        if (path.length != 2) {
            sendResponse(httpExchange, HTTP_NOT_FOUND, gson.toJson(new RequestError("Invalid URL")));
            return;
        }

        String body = getRequestBody(httpExchange);
        Epic task = gson.fromJson(body, Epic.class);
        long id = task.getId();

        if (id == 0) {
            task = taskService.createEpicTask(task);
            sendResponse(httpExchange, HTTP_CREATED, gson.toJson(task));
        } else {
            task = taskService.updateEpicTask(task);
            sendResponse(httpExchange, HTTP_OK, gson.toJson(task));
        }
    }

    private void deleteHandler(HttpExchange httpExchange) throws IOException {
        String[] path = httpExchange.getRequestURI().getPath().split("/");

        if (path.length == 2) {
            taskService.removeAllEpicTasks();
            sendResponseNoContent(httpExchange);
            return;
        }

        if (path.length == 3) {
            long id = getIdFromRequest(path[2]);
            taskService.removeEpicTaskById(id);
            sendResponseNoContent(httpExchange);
            return;
        }
        // 404 с некорректным урлом
        sendResponse(httpExchange, HTTP_NOT_FOUND, gson.toJson(new RequestError("Invalid URL")));
    }
}