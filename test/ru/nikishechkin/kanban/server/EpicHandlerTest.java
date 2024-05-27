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
import ru.nikishechkin.kanban.model.Epic;
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

public class EpicHandlerTest {

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
    public void getEpics_ReturnAllEpics() {

        URI url = URI.create("http://localhost:8080/epics");
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

            List<Epic> listEpics = gson.fromJson(jsonElement, new EpicListTypeToken().getType());
            assertEquals(manager.getEpics(), listEpics);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEpicId_Exist_ReturnCorrectEpic() {

        URI url = URI.create("http://localhost:8080/epics/4");
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

            Epic epic = gson.fromJson(jsonElement, Epic.class);
            assertEquals(manager.getEpicById(4), epic);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEpicById_NotExist_Error() {

        URI url = URI.create("http://localhost:8080/epics/40");
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
    public void addNewEpic_EpicAdded() {

        Epic newEpic = new Epic("Эпик НОВЫЙ", "Описание");

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // Если не задать данные параметры, то при конвертации объекта в Json вылетает NullPointerException
        // Как этого избежать?
        newEpic.setStartTime(LocalDateTime.of(2050, 01, 02, 20, 30));
        newEpic.setEndTime(LocalDateTime.of(2050, 01, 02, 20, 40));
        newEpic.setDuration(Duration.ofMinutes(10));
        newEpic.setStatus(TaskStatus.NEW);
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        String taskJson = gson.toJson(newEpic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Некорректный код ответа");
            assertEquals(4, manager.getEpics().size(), "Некорректное количество эпиков");
            assertEquals(newEpic.getName(), manager.getEpics().get(3).getName(), "Некорректное имя эпика");

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void editEpic_CorrectParams_EpicEdited() {

        Epic editEpic = manager.getEpicById(0);
        editEpic.setName("Эпик ОБНОВЛЕННЫЙ");
        String taskJson = gson.toJson(editEpic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Некорректный код ответа");
            assertEquals(3, manager.getEpics().size(), "Некорректное количество эпиков");
            assertEquals(editEpic.getName(), manager.getEpicById(0).getName(), "Некорректное имя эпика");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteEpic_Exist_EpicDeleted() {

        URI url = URI.create("http://localhost:8080/epics/4");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Некорректный код ответа");
            assertEquals(2, manager.getEpics().size(), "Некорректное количество эпиков");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteEpic_NotExist_Error() {

        URI url = URI.create("http://localhost:8080/epics/404");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Некорректный код ответа");
            assertEquals(3, manager.getEpics().size(), "Некорректное количество эпиков");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException(e);
        }
    }
}
