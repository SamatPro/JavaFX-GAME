package ru.kpfu.itis.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.kpfu.itis.enums.Action;
import ru.kpfu.itis.protocol.Message;
import ru.kpfu.itis.protocol.MessageType;
import ru.kpfu.itis.sockets.ClientSocket;
import ru.kpfu.itis.util.GameUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private ClientSocket clientSocket;

    private static final String PLAYER_ICON = "src/main/resources/img/img.png";

    @FXML
    private Button sendMessageButton;

    @FXML
    private TextField messageText;

    @FXML
    private VBox messages;

    @FXML
    private TextField name;

    @FXML
    private Button connectButton;

    @FXML
    private Label helloLabel;

    @FXML
    private ImageView player;

    @FXML
    private ImageView enemy;

    @FXML
    private AnchorPane gameArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image playerIcon = new Image(new FileInputStream(PLAYER_ICON));

            player.setImage(playerIcon);
            player.setRotate(180);

            enemy.setImage(playerIcon);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Platform.runLater( () -> gameArea.requestFocus() );
        connectButton.setOnMouseClicked(event -> {
            String nickname = name.getText();
            name.setEditable(false);
            helloLabel.setText("Привет, " + nickname + "!");
            clientSocket = new ClientSocket();
            clientSocket.connect(this, nickname);
            clientSocket.start();
        });
        sendMessageButton.setOnMouseClicked(event -> {
            sendMessage();
        });

    }

    private final EventHandler<KeyEvent> playerControlEvent = event -> {
        switch (event.getCode()) {
            case RIGHT: {
                //1
                GameUtils.goRight(player);
                Message message = createActionMessage(Action.RIGHT);
                clientSocket.sendMessage(message);
                System.out.println(message.toString());
                break;
            }
            case LEFT: {
                //2
                GameUtils.goLeft(player);
                Message message = createActionMessage(Action.LEFT);
                clientSocket.sendMessage(message);
                System.out.println(Action.LEFT.getDescription());
                break;
            }
            case ALT_GRAPH: {
                //3
                GameUtils.shoot(player, enemy, gameArea, false);
                Message message = createActionMessage(Action.SHOOT);
                clientSocket.sendMessage(message);
                System.out.println(Action.SHOOT.getDescription());
                break;
            }
            case ESCAPE: {
                //TODO выход из игры
                break;
            }
            case ENTER: {
                sendMessage();
                break;
            }

        }
    };

    private Message createActionMessage(Action action) {
        Message message = new Message();
        message.setType(MessageType.ACTION);
        message.setBody(action.getTitle());
        return message;
    }

    private Message createChatMessage(String text) {
        Message message = new Message();
        message.setType(MessageType.CHAT);
        message.setBody(text);

        return message;
    }

    private void sendMessage() {
        System.out.println(messageText.getText());
        Label messageLabel = new Label();
        messageLabel.setText("Я: " + messageText.getText());
        messages.getChildren().add(messageLabel);

        clientSocket.sendMessage(createChatMessage(messageText.getText()));

        messageText.clear();
    }

    public EventHandler<KeyEvent> getPlayerControlEvent() {
        return playerControlEvent;
    }

    public VBox getMessages() {
        return messages;
    }

    public ImageView getPlayer() {
        return player;
    }

    public ImageView getEnemy() {
        return enemy;
    }

    public AnchorPane getGameArea() {
        return gameArea;
    }
}
