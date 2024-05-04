package ru.gb.lesson5.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gb.lesson5.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Инкапсуляция для клиента на сервере
 */
public class ClientConnection implements Runnable {

    private final Server server;
    private final Socket socket;
    private final Scanner input;
    private final PrintWriter output;
    private final String login;
    private Runnable onCloseHandler;
    public final boolean connected;

    public ClientConnection(Socket socket, Server server) throws IOException, ClassNotFoundException {
        this.server = server;
        this.socket = socket;
        this.input = new Scanner(socket.getInputStream());
        this.output = new PrintWriter(socket.getOutputStream(), true);

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = input.nextLine();
        Message message = objectMapper.readValue(msg, Message.class);
        if (message ==  null || message.getFrom().isEmpty())
        {
            this.connected = false;
            this.login = "";
            return;
        }

        this.connected = true;
        this.login = message.getFrom();
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public String getLogin() {
        return login;
    }

    public void setOnCloseHandler(Runnable onCloseHandler) {
        this.onCloseHandler = onCloseHandler;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msgFromClient = input.nextLine(); // json

                // TODO: распарсить сообщение и понять, что нужно сделать
                ObjectMapper objectMapper = new ObjectMapper();
                Message message;
                try {
                    message = objectMapper.readValue(msgFromClient, Message.class);
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
                if (message.isExit())
                    break;
                if (message.getTo().isEmpty())
                    message.setBroadcast(true);

                if (!message.isBroadcast()) {
                    server.sendMessageToClient(message);
                } else {
                   server.sendMessageToAll(message);
                }
            }

            try {
                close();
            } catch (IOException e) {
                System.err.println("Произошла ошибка во время закрытия сокета: " + e.getMessage());
            }
        } finally {
            if (onCloseHandler != null) {
                onCloseHandler.run();
            }
        }
    }

    public void close() throws IOException {
        socket.close();
    }

}
