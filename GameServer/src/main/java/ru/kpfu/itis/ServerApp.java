package ru.kpfu.itis;

import ru.kpfu.itis.sockets.GameServer;

public class ServerApp {

    public static void main(String[] args) {

        GameServer gameServer = GameServer.getInstance();
        gameServer.start();

    }
}
