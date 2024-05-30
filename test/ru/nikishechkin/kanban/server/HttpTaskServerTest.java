package ru.nikishechkin.kanban.server;

import org.junit.jupiter.api.Test;
import ru.nikishechkin.kanban.manager.Managers;
import ru.nikishechkin.kanban.manager.task.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    @Test
    void start() {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        try {
            httpTaskServer.start();
            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
            assertTrue(socket.isConnected());
            socket.close();
            httpTaskServer.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}