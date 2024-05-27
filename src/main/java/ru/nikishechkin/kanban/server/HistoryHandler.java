package ru.nikishechkin.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.nikishechkin.kanban.manager.task.TaskManager;
import ru.nikishechkin.kanban.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String[] uri = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
        try {
            if (uri.length == 2) {
                if (exchange.getRequestMethod().equals("GET")) {
                    // Возврат истории просмотра задач
                    sendText(exchange, gson.toJson(taskManager.getHistory()));
                } else {
                    sendResponse(exchange, "Ошибка в запросе", 404);
                }
            } else {
                sendResponse(exchange, "Ошибка в запросе", 404);
            }
        } catch (Exception e) {
            sendResponse(exchange, "Ошибка в запросе", 404);
        }
    }
}
