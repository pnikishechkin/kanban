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
import ru.nikishechkin.kanban.model.SubTask;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskHandlerTest {

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
            Files.copy(Path.of("resources\\defaultTasks.csv"), Path.of("resources\\tasks.csv"),
                    StandardCopyOption.REPLACE_EXISTING);
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
    public void getSubtasks_ReturnAllSubtasks() {

        URI url = URI.create("http://localhost:8080/subtasks");
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

            List<SubTask> listSubtasks = gson.fromJson(jsonElement, new SubtaskListTypeToken().getType());
            assertEquals(manager.getSubTasks(), listSubtasks);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubtaskById_Exist_ReturnCorrectSubtask() {

        URI url = URI.create("http://localhost:8080/subtasks/1");
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

            SubTask subtask = gson.fromJson(jsonElement, SubTask.class);
            assertEquals(manager.getSubTaskById(1), subtask);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubtaskById_NotExist_Error() {

        URI url = URI.create("http://localhost:8080/subtasks/40");
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
    public void addNewSubtask_SubtaskAdded() {

        SubTask newSubtask = new SubTask("Подзадача НОВАЯ", "Описание",
                LocalDateTime.of(2029, 01, 02, 20, 30),
                Duration.ofMinutes(10), 0);
        String taskJson = gson.toJson(newSubtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Некорректный код ответа");
            assertEquals(9, manager.getSubTasks().size(), "Некорректное количество подзадач");
            assertEquals(newSubtask.getName(), manager.getSubTasks().get(8).getName(), "Некорректное имя подзадачи");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addNewSubtask_InteractError_SubtaskNotAdded() {

        SubTask newSubtask = new SubTask("Подзадача ПЕРЕСЕКАЮЩАЯСЯ", "Описание",
                LocalDateTime.of(2024, 9, 1, 9, 30),
                Duration.ofMinutes(10), 0);
        String taskJson = gson.toJson(newSubtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode(), "Некорректный код ответа");
            assertEquals(8, manager.getSubTasks().size(), "Некорректное количество подзадач");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void editSubtask_CorrectParams_SubtaskEdited() {

        SubTask editSubtask = new SubTask(1, "Подзадача ОБНОВЛЕННАЯ", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2024, 01, 01, 20, 30),
                Duration.ofMinutes(10), 0);
        String taskJson = gson.toJson(editSubtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Некорректный код ответа");
            assertEquals(8, manager.getSubTasks().size(), "Некорректное количество подзадач");
            assertEquals(editSubtask.getName(), manager.getSubTaskById(1).getName(), "Некорректное имя подзадачи");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void editSubtask_InteractionsError_SubtaskNotEdited() {

        SubTask editSubtask = new SubTask(1, "Подзадача ОБНОВЛЕННАЯ", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2024, 10, 10, 9, 30),
                Duration.ofMinutes(10), 0);
        String taskJson = gson.toJson(editSubtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode(), "Некорректный код ответа");
            assertEquals(8, manager.getSubTasks().size(), "Некорректное количество подзадач");
            assertNotEquals(editSubtask.getName(), manager.getSubTaskById(1).getName(), "Некорректное имя подзадачи");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void editSubtask_NotExist_Error() {

        Task editTask = new Task(16, "Задача НЕ СУЩЕСТВУЮЩАЯ", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2024, 10, 10, 9, 30),
                Duration.ofMinutes(10));
        String taskJson = gson.toJson(editTask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Некорректный код ответа");
            assertEquals(2, manager.getTasks().size(), "Некорректное количество подзадач");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteSubtask_Exist_SubtaskDeleted() {

        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Некорректный код ответа");
            assertEquals(7, manager.getSubTasks().size(), "Некорректное количество подзадач");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteSubtask_NotExist_Error() {

        URI url = URI.create("http://localhost:8080/subtasks/404");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Некорректный код ответа");
            assertEquals(8, manager.getSubTasks().size(), "Некорректное количество подзадач");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

}
