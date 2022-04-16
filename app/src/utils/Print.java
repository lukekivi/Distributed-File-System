package utils;

import data.ServerInfo;
import data.Command;
import data.CommandType;
import pa3.ReadResponse;
import pa3.WriteResponse;

/**
 * Print class used mostly for convenience during testing.
 */
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

    public static void ReadResponse(ReadResponse readResponse) {
        System.out.println(
            "ReadResponse:" +
            "\n\t-  Status: " + readResponse.status +
            "\n\t-     msg: " + readResponse.msg +
            "\n\t- File ID: " + readResponse.file.id +
            "\n\t- version: " + readResponse.file.version
        );
    }

    public static void WriteResponse(WriteResponse writeResponse) {
        System.out.println(
            "WriteResponse:" +
        "\n\t-  Status: " + writeResponse.status +
        "\n\t-     msg: " + writeResponse.msg
        );
    }
}
