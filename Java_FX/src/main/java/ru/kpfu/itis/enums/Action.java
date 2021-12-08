package ru.kpfu.itis.enums;

import javafx.scene.input.KeyCode;
import ru.kpfu.itis.protocol.MessageType;

public enum Action {

    RIGHT(0, "right", "Игрок передвинулся направо", KeyCode.RIGHT),
    LEFT(1, "left", "Игрок передвинулся налево", KeyCode.LEFT),
    SHOOT(5, "shoot", "Игрок стреляет", KeyCode.ALT_GRAPH);

    private final int code;
    private final String title;
    private final String description;
    private final KeyCode keyCode;

    Action(int code, String title, String description, KeyCode keyCode) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.keyCode = keyCode;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public static Action typeOf(String title){
        for (Action action: Action.values()) {
            if (action.getTitle().equalsIgnoreCase(title)) {
                return action;
            }
        }
        return null;
    }

}
