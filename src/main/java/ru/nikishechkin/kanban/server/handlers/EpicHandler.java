package ru.nikishechkin.kanban.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.nikishechkin.kanban.exceptions.NotFoundException;
import ru.nikishechkin.kanban.exceptions.OverlapException;
import ru.nikishechkin.kanban.manager.task.TaskManager;
import ru.nikishechkin.kanban.model.Epic;
import ru.nikishechkin.kanban.server.adapters.DurationTypeAdapter;
import ru.nikishechkin.kanban.server.adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
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
        //  /epics
        if (uri.length == 2) {
            // Возврат всех эпиков
            sendText(exchange, gson.toJson(taskManager.getEpics()));
        }
        //  /epics/{id}
        else if (uri.length == 3) {
            int epicId = Integer.parseInt(uri[2]);
            Epic epic = taskManager.getEpicById(epicId);

            // Получение эпика с идентификатором epicId
            sendText(exchange, gson.toJson(epic));
        }
        // другой путь
        else {
            sendResponse(exchange, "Ошибка в запросе", 404);
        }
    }

    private void postHandle(HttpExchange exchange, String[] uri) throws IOException, RuntimeException {
        //  /epics
        if (uri.length == 2) {
            // Добавление нового эпика
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic newEpic = gson.fromJson(body, Epic.class);
            if (newEpic == null) {
                sendResponse(exchange, "Ошибка в запросе", 404);
            }
            // id не передан - новый эпик
            if (newEpic.getId() == null) {
                taskManager.addEpic(newEpic);
                sendModified(exchange);
            } else { // id передан - обновление имеющегося эпика
                taskManager.editEpic(newEpic);
                sendModified(exchange);
            }
        }
        // другой путь
        else {
            sendResponse(exchange, "Ошибка в запросе", 404);
        }
    }

    private void deleteHandle(HttpExchange exchange, String[] uri) throws IOException, RuntimeException {
        //  /epics/{id}
        if (uri.length == 3) {
            int epicId = Integer.parseInt(uri[2]);
            Epic epic = taskManager.getEpicById(epicId);

            // Удаление эпика с идентификатором epicId
            taskManager.deleteEpic(epicId);
            sendModified(exchange);
        }
        // другой путь
        else {
            sendResponse(exchange, "Ошибка в запросе", 404);
        }
    }

}

class EpicListTypeToken extends TypeToken<List<Epic>> {

}
