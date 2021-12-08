package ru.kpfu.itis.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.kpfu.itis.protocol.Message;
import ru.kpfu.itis.protocol.MessageType;
import ru.kpfu.itis.sockets.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Client extends Thread {

    private String token;
    private String nickname;

    private Socket client;
    // поток символов, которые мы отправляем клиенту
    private PrintWriter toClient;
    // поток символов, которые мы читаем от клиента
    private BufferedReader fromClient;

    //сервер, который хранит список других клиентов
    private GameServer gameServer;

    public Client(Socket client) {
        this.gameServer = GameServer.getInstance();
        this.client = client;
        try {
            // обернули потоки байтов в потоки символов
            this.toClient = new PrintWriter(client.getOutputStream(), true);
            this.fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // мы в любое время можем получить сообщение от клиента
    // поэтому чтение сообщения от клиента должно происходить в побочном потоке
    @Override
    public void run() {
        while (true) {
            String messageFromClient;
            try {
                // прочитали сообщение от клиента
                messageFromClient = fromClient.readLine();

                if (messageFromClient != null) {
                    System.out.println("SERVER: получено сообщение от " + nickname + "<" + messageFromClient + ">");
                    Message message = new ObjectMapper().readValue(messageFromClient, Message.class);
                    System.out.println(message.toString());
                    //2
                    switch (message.getType()) {
                        case CONNECT: {
                            nickname = message.getHeader("nickname");

                            setToken(UUID.randomUUID().toString());
                            Message sendMessage = new Message();
                            sendMessage.setType(MessageType.CONNECT);
                            sendMessage.addHeader("token", getToken());
                            sendMessage(sendMessage);

                            //Уведомляем других о присоединении
                            List<Client> clients = new ArrayList<>(gameServer.getClients());

                            Message notifyMembers = new Message();
                            notifyMembers.setType(MessageType.CHAT);
                            notifyMembers.setBody("Присоединился " + nickname);
                            clients.remove(this);
                            for (Client client: clients) {
                                client.sendMessage(message);
                            }
                            break;
                        }
                        case ACTION: {
                            List<Client> clients = new ArrayList<>(gameServer.getClients());
                            System.out.println(clients.toString());
                            clients.remove(this);
                            for (Client client: clients) {
                                client.sendMessage(message);
                            }
                            break;
                        }
                        case CHAT: {
                            List<Client> clients = new ArrayList<>(gameServer.getClients());
                            message.setBody(this.nickname + ": " + message.getBody());
                            System.out.println(clients.toString());

                            clients.remove(this);
                            for (Client client: clients) {
                                client.sendMessage(message);
                            }
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                gameServer.removeClient(this);
                try {
                    fromClient.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                toClient.close();
            }
        }
    }

    public void sendMessage(Message message) {
        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            System.out.println("SEND messageTO: " + nickname + " " + jsonMessage);
            toClient.println(jsonMessage);
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
