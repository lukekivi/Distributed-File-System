package utils;

import data.ServerInfo;

public class Print {
    public static void serverInfo(ServerInfo serverInfo) {
        System.out.println("Server ID: " + serverInfo.getId() +
            "\n\t-      ip: " + serverInfo.getIp() +
            "\n\t-    port: " + serverInfo.getPort() +
            "\n\t- isCoord: " + serverInfo.isCoord()
        );
    }
}
