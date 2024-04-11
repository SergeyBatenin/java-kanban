package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskIdConvertException;
import exception.TaskTimeIntersectionException;
import service.TaskService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.net.HttpURLConnection.*;

public abstract class BaseHandler implements HttpHandler {
    protected final TaskService taskService;
    protected final Gson gson;

    public BaseHandler(TaskService taskService) {
        this.taskService = taskService;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    abstract void generateAnsSendResponse(HttpExchange httpExchange) throws IOException;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            try {
                generateAnsSendResponse(httpExchange);
            } catch (TaskIdConvertException e) {
                sendResponse(httpExchange, HTTP_BAD_REQUEST, gson.toJson(new RequestError(e.getMessage())));
            } catch (TaskTimeIntersectionException | JsonSyntaxException e) {
                sendResponse(httpExchange, HTTP_NOT_ACCEPTABLE, gson.toJson(new RequestError(e.getMessage())));
            } catch (Exception e) {
                sendResponse(httpExchange, HTTP_INTERNAL_ERROR, gson.toJson(new RequestError(e.getMessage())));
            }
        }
    }

    protected long getIdFromRequest(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new TaskIdConvertException("Передан некорректный id при запросе", e);
        }
    }

    protected void sendResponse(HttpExchange httpExchange, int statusCode, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendResponseNoContent(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(HTTP_NO_CONTENT, -1);
    }

    protected String getRequestBody(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
