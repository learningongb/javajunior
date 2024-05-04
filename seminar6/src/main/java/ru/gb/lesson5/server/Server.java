package ru.gb.lesson5.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gb.lesson5.message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Map<String, ClientConnection> clients = new HashMap<>();

    public void start(int port) {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Подключился новый клиент: " + clientSocket);
                ClientConnection client;

                client = new ClientConnection(clientSocket, this);
                if (!client.connected) {
                    System.out.println("Ошибка при установке соединения с клиентом");
                    client.sendMessage("Ошибка при установке соединения");
                    client.close();
                    continue;
                }

                String clientLogin = client.getLogin();

                if (clients.containsKey(clientLogin)) {
                    System.out.println("Сеанс принудительно завершен");
                    client.sendMessage("Пользователь с таким логином уже подключен");
                    client.close();
                    continue;
                }

                clients.put(clientLogin, client);
                System.out.println("Логин пользователя " + clientLogin);
                sendMessageToAll(new Message("", "Подключился новый клиент: " + clientLogin));

                client.setOnCloseHandler(() -> {
                    clients.remove(clientLogin);
                    System.out.println("Клиент " + clientLogin + " отключился");
                    sendMessageToAll(new Message("", "Клиент " + clientLogin + " отключился"));
                });

                new Thread(client).start();
            }
        } catch (IOException e) {
            System.err.println("Произошла ошибка во время прослушивания порта " + port + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToClient(Message message) {
        ClientConnection client = clients.get(message.getTo());
        if (client == null) {
            // Клиент не найден на сервере
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            client.sendMessage(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());;
        }
    }

    public void sendMessageToAll(Message message) {
        Message newMessage = new Message(message.getFrom(), message.getTo(), message.getMessage(), true);
        ObjectMapper objectMapper = new ObjectMapper();
        for (ClientConnection client : clients.values()) {
            newMessage.setTo(client.getLogin());
            try {
                client.sendMessage(objectMapper.writeValueAsString(newMessage));
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());;
            }
        }
    }

}
