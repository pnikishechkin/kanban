package ru.nikishechkin.kanban.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import ru.nikishechkin.kanban.manager.Managers;
import ru.nikishechkin.kanban.manager.task.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServer {

    private HttpServer server;
    private TaskManager taskManager;

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {

        // Загрузка менеджера задач из файла (для удобства проверки)
        try {
            Files.copy(Path.of("resources\\defaultTasks.csv"), Path.of("resources\\tasks.csv"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpTaskServer server = new HttpTaskServer(Managers.getFileBackedTaskManager("resources\\tasks.csv"));
        server.start();

    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));

        server.start();
    }

    public void stop() throws IOException {
        if (server != null) {
            server.stop(0);
        }
    }
}
