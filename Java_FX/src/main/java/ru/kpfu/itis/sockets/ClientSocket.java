package ru.kpfu.itis.sockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import ru.kpfu.itis.controller.MainController;
import ru.kpfu.itis.enums.Action;
import ru.kpfu.itis.protocol.Message;
import ru.kpfu.itis.protocol.MessageType;
import ru.kpfu.itis.util.GameUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocket extends Thread {

    private Socket clientSocket;

    private final String HOST = "localhost";
    private final int PORT = 8888;

    private PrintWriter out;
    private BufferedReader fromServer;

    private String token;
    private String nickname;
    private MainController mainController;

    public void connect(MainController mainController, String nickname) {
        this.mainController = mainController;
        try {
            clientSocket = new Socket(HOST, PORT);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Не удалось подсоединиться");
//            throw new RuntimeException();
        }
        this.nickname = nickname;
        Message message = new Message();
        message.setType(MessageType.CONNECT);
        message.addHeader("nickname", nickname);
        sendMessage(message);
    }

    public void sendMessage(Message message) {
        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            System.out.println(jsonMessage);
            out.println(jsonMessage);
        } catch (JsonProcessingException e) {
            // console log
        }
    }

    //4
    @Override
    public void run() {
        while (true) {
            String messageFromServer;
            Message message = null;
            try {
                messageFromServer = fromServer.readLine();
                System.out.println(messageFromServer);
                message = new ObjectMapper().readValue(messageFromServer, Message.class);
                System.out.println(message);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            if (message != null) {
                switch (message.getType()) {
                    case CONNECT:
                        this.token = message.getHeader("token");
                        break;
                    case ACTION:
                        //5
                        switch (Action.typeOf(message.getBody())) {
                            case RIGHT: {
                                Platform.runLater(() -> GameUtils.goLeft(mainController.getEnemy()));
                                break;
                            }
                            case LEFT: {
                                Platform.runLater(() -> GameUtils.goRight(mainController.getEnemy()));
                                break;
                            }
                            case SHOOT: {
                                Platform.runLater(() -> GameUtils.shoot(mainController.getEnemy(), mainController.getPlayer(), mainController.getGameArea(), true));
                                break;
                            }
                        }
                        break;
                    case CHAT: {
                        Label label = new Label();
                        label.setText(message.getBody());
                        label.setFont(Font.font("Arial"));
                        Platform.runLater(() -> mainController.getMessages().getChildren().add(label));
                        break;
                    }
                    default: {
                        System.out.println("Неизвестно!!!");
                    }
                }
            }
        }
    }
}
