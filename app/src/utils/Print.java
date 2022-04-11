package utils;

import data.ServerInfo;
import data.Command;
import data.CommandType;

public class Print {
    public static void serverInfo(ServerInfo serverInfo) {
        System.out.println(
            "Server ID: " + 
            "\n\t-      id: " + serverInfo.getId() +
            "\n\t-      ip: " + serverInfo.getIp() +
            "\n\t-    port: " + serverInfo.getPort() +
            "\n\t- isCoord: " + serverInfo.isCoord()
        );
    }

    public static void command(Command command) {
        System.out.print(
            "Command:" +
            "\n\t-   type: " + command.getCommandType()
        );

        if (command.getCommandType() != CommandType.PRINT) {
            System.out.println("\n\t- fileId: " + command.getFileId());
        } else {
            System.out.println();
        }
    }
}
