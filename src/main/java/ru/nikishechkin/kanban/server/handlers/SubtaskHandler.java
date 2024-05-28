package ru.nikishechkin.kanban.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.nikishechkin.kanban.exceptions.NotFoundException;
import ru.nikishechkin.kanban.exceptions.OverlapException;
import ru.nikishechkin.kanban.manager.task.TaskManager;
import ru.nikishechkin.kanban.model.SubTask;
import ru.nikishechkin.kanban.server.adapters.DurationTypeAdapter;
import ru.nikishechkin.kanban.server.adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
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
            //  /subtasks
            if (uri.length == 2) {
                subTaskRequest(exchange);
            }
            //  /subtasks/{id}
            else if (uri.length == 3) {
                subTaskIdRequest(exchange, uri[2]);
            }
            // другой путь
            else {
                sendResponse(exchange, "Ошибка в запросе", 404);
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, "Ошибка в запросе", 404);
        } catch (NotFoundException ex) {
            sendNotFound(exchange);
        } catch (OverlapException ex) {
            sendHasInteractions(exchange);
        }
    }

    /**
     * Обработка запросов с адресом "/subtask"
      * @param exchange
     * @throws IOException
     */
    private void subTaskRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            // Возврат всех подзадач
            sendText(exchange, gson.toJson(taskManager.getSubTasks()));
        } else if (exchange.getRequestMethod().equals("POST")) {
            // Добавление новой подзадачи
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            SubTask newTask = gson.fromJson(body, SubTask.class);
            if (newTask == null) {
                sendResponse(exchange, "Ошибка в запросе", 404);
            }
            // id не передан - новая подзадача
            if (newTask.getId() == null) {
                taskManager.addSubTask(newTask);
                sendModified(exchange);
            } else { // id передан - обновление имеющейся подзадачи
                taskManager.editSubTask(newTask);
                sendModified(exchange);
            }
        }
    }

    /**
     * Обработка запросов с адресом "/subtask/{id}"
     * @param exchange
     * @throws IOException
     */
    private void subTaskIdRequest(HttpExchange exchange, String strId) throws IOException {
        int taskId = Integer.parseInt(strId);
        SubTask task = taskManager.getSubTaskById(taskId);

        if (exchange.getRequestMethod().equals("GET")) {
            // Получение подзадачи с идентификатором taskId
            sendText(exchange, gson.toJson(task));
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            // Удаление подзадачи с идентификатором taskId
            taskManager.deleteSubTask(taskId);
            sendModified(exchange);
        }
    }
}

class SubtaskListTypeToken extends TypeToken<List<SubTask>> {

}
