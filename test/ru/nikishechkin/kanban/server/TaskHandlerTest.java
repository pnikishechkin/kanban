package ru.nikishechkin.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.manager.Managers;
import ru.nikishechkin.kanban.manager.task.TaskManager;
import ru.nikishechkin.kanban.model.Task;
import ru.nikishechkin.kanban.model.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {

    private HttpTaskServer server;
    private TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void startServer() {

        // Загрузка менеджера задач из файла (для удобства тестирования)
        try {
            Files.copy(Path.of("resources\\defaultTasks.csv"), Path.of("resources\\tasks.csv"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        manager = Managers.getFileBackedTaskManager("resources\\tasks.csv");
        server = new HttpTaskServer(manager);

        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void stopServer() {
        try {
            server.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void GetTasks_ReturnAllTasks() {

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Некорректный код ответа");

            JsonElement jsonElement = JsonParser.parseString(response.body());
            assertTrue(jsonElement.isJsonArray());

            List<Task> listTasks = gson.fromJson(jsonElement, new TaskListTypeToken().getType());
            assertEquals(manager.getTasks(), listTasks);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void GetTaskById_Exist_ReturnCorrectTask() {

        URI url = URI.create("http://localhost:8080/tasks/11");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Некорректный код ответа");

            JsonElement jsonElement = JsonParser.parseString(response.body());
            assertTrue(jsonElement.isJsonObject());

            Task task = gson.fromJson(jsonElement, Task.class);
            assertEquals(manager.getTaskById(11), task);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void GetTaskById_NotExist_Error() {

        URI url = URI.create("http://localhost:8080/tasks/40");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Некорректный код ответа");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void AddNewTask_TaskAdded() {

        Task newTask = new Task("Задача НОВАЯ", "Описание",
                LocalDateTime.of(2029, 01, 02, 20, 30),
                Duration.ofMinutes(10));
        String taskJson = gson.toJson(newTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Некорректный код ответа");
            assertEquals(3, manager.getTasks().size(), "Некорректное количество задач");
            assertEquals(newTask.getName(), manager.getTasks().get(2).getName(), "Некорректное имя задачи");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void AddNewTask_InteractError_TaskNotAdded() {

        Task newTask = new Task("Задача ПЕРЕСЕКАЮЩАЯСЯ", "Описание",
                LocalDateTime.of(2024, 9, 1, 9, 30),
                Duration.ofMinutes(10));
        String taskJson = gson.toJson(newTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode(), "Некорректный код ответа");
            assertEquals(2, manager.getTasks().size(), "Некорректное количество задач");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void EditTask_CorrectParamsTask_TaskEdited() {

        Task editTask = new Task(11, "Задача ОБНОВЛЕННАЯ", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2029, 01, 02, 20, 30),
                Duration.ofMinutes(10));
        String taskJson = gson.toJson(editTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Некорректный код ответа");
            assertEquals(2, manager.getTasks().size(), "Некорректное количество задач");
            assertEquals(editTask.getName(), manager.getTaskById(11).getName(), "Некорректное имя задачи");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void EditTask_InteractionsError_TaskNotEdited() {

        Task editTask = new Task(11, "Задача ОБНОВЛЕННАЯ", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2024, 10, 10, 9, 30),
                Duration.ofMinutes(10));
        String taskJson = gson.toJson(editTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode(), "Некорректный код ответа");
            assertEquals(2, manager.getTasks().size(), "Некорректное количество задач");
            assertNotEquals(editTask.getName(), manager.getTaskById(11).getName(), "Некорректное имя задачи");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void EditTask_NotExist_Error() {

        Task editTask = new Task(16, "Задача НЕ СУЩЕСТВУЮЩАЯ", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2024, 10, 10, 9, 30),
                Duration.ofMinutes(10));
        String taskJson = gson.toJson(editTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Некорректный код ответа");
            assertEquals(2, manager.getTasks().size(), "Некорректное количество задач");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void DeleteTask_Exist_TaskDeleted() {

        URI url = URI.create("http://localhost:8080/tasks/11");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Некорректный код ответа");
            assertEquals(1, manager.getTasks().size(), "Некорректное количество задач");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void DeleteTask_NotExist_Error() {

        URI url = URI.create("http://localhost:8080/tasks/404");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Некорректный код ответа");
            assertEquals(2, manager.getTasks().size(), "Некорректное количество задач");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }
}
