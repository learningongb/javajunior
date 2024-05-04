package ru.gb.lesson5.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gb.lesson5.message.Message;
import ru.gb.lesson5.server.ServerMain;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        System.out.println("Введите ваш логин: ");
        String login = console.nextLine();

        Socket server = new Socket(ServerMain.SERVER_ADDR, ServerMain.SERVER_PORT);
        System.out.println("Подключение к серверу успешно");
        Scanner in = new Scanner(server.getInputStream());
        PrintWriter out = new PrintWriter(server.getOutputStream(), true);

        Message message = new Message(login);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(message);
        out.println(json);

        // Поток на чтение
        new Thread(() -> {
            while (true) {

                String message1 = "";
                try {
                    message1 = in.nextLine();
                } catch (NoSuchElementException e) {
                    System.exit(0);
                };

                ObjectMapper objectMapper1 = new ObjectMapper();

                try {
                    Message newMessage = objectMapper1.readValue(message1, Message.class);
                    String from;
                    if (newMessage.getFrom().isEmpty())
                        from = "Сообщение от сервера: ";
                    else
                        from = "Сообщение от " + newMessage.getFrom() + ": ";
                    System.out.println(from + newMessage.getMessage());
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();

        // Поток на запись
        new Thread(() -> {
            while (true) {
                String inputFromConsole = console.nextLine();
                Message newMessage = new Message();
                newMessage.setFrom(login);
                if (Objects.equals("exit", inputFromConsole)) {
                    newMessage.setExit(true);
                } else {
                    String[] split = inputFromConsole.split("\\s+");
                    String loginTo = "";
                    if (split.length > 1) {
                        loginTo = split[0].substring(1);
                    }

                    if (loginTo.isEmpty()) {
                        newMessage.setBroadcast(true);
                        newMessage.setMessage(inputFromConsole);

                    } else {
                        newMessage.setTo(loginTo);
                        String pureMessage = inputFromConsole.replace("@" + loginTo + " ", "");
                        newMessage.setMessage(pureMessage);
                    }
                }
                try {
                    String jsonMessage = new ObjectMapper().writeValueAsString(newMessage);
                    out.println(jsonMessage);
                } catch (JsonProcessingException e) {
                    System.out.println("Ошибка при отправке сообщения: " + e.getMessage());
                }
            }
        }).start();
    }

}



