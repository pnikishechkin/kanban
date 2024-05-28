package ru.nikishechkin.kanban.server.handlers;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.nikishechkin.kanban.exceptions.NotFoundException;
import ru.nikishechkin.kanban.exceptions.OverlapException;
import ru.nikishechkin.kanban.manager.task.TaskManager;
import com.google.gson.*;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.server.adapters.DurationTypeAdapter;
import ru.nikishechkin.kanban.server.adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    private Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String[] uri = exchange.getRequestURI().getPath().split("/");

        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    this.getHandle(exchange, uri);
                    break;
                case "POST":
                    this.postHandle(exchange, uri);
                    break;
                case "DELETE":
                    this.deleteHandle(exchange, uri);
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, "Ошибка в запросе", 404);
        } catch (NotFoundException ex) {
            sendNotFound(exchange);
        } catch (OverlapException ex) {
            sendHasInteractions(exchange);
        }
    }

    private void getHandle(HttpExchange exchange, String[] uri) throws IOException {
        //  /tasks
        if (uri.length == 2) {
            sendText(exchange, gson.toJson(taskManager.getTasks()));
        }
        //  /tasks/{id}
        else if (uri.length == 3) {
            int taskId = Integer.parseInt(uri[2]);
            Task task = taskManager.getTaskById(taskId);

            // Получение задачи с идентификатором taskId
            sendText(exchange, gson.toJson(task));
        }
        // другой путь
        else {
            sendResponse(exchange, "Ошибка в запросе", 404);
        }
    }

    private void postHandle(HttpExchange exchange, String[] uri) throws IOException, RuntimeException {
        //  /tasks
        if (uri.length == 2) {
            // Добавление новой задачи
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task newTask = gson.fromJson(body, Task.class);
            if (newTask == null) {
                sendResponse(exchange, "Ошибка в запросе", 404);
            }
            // id не передан - новая задача
            if (newTask.getId() == null) {
                taskManager.addTask(newTask);
                sendModified(exchange);
            } else { // id передан - обновление имеющейся задачи
                taskManager.editTask(newTask);
                sendModified(exchange);
            }
        }
        // другой путь
        else {
            sendResponse(exchange, "Ошибка в запросе", 404);
        }
    }

    private void deleteHandle(HttpExchange exchange, String[] uri) throws IOException, RuntimeException {
        //  /tasks/{id}
        if (uri.length == 3) {
            int taskId = Integer.parseInt(uri[2]);
            Task task = taskManager.getTaskById(taskId);

            // Удаление задачи с идентификатором taskId
            taskManager.deleteTask(taskId);
            sendModified(exchange);
        }
        // другой путь
        else {
            sendResponse(exchange, "Ошибка в запросе", 404);
        }
    }
}


class TaskListTypeToken extends TypeToken<List<Task>> {

}
