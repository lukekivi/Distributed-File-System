package client;

import utils.RPC.ServerConnFactory;
import utils.RPC.ServerConn;
import utils.Print;
import utils.Log;
import data.Command;
import data.ServerInfo;
import utils.Config;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.TException;
import pa3.Server;
import data.CommandType;
import pa3.ReadResponse;
import pa3.StructResponse;
import pa3.WriteResponse;


public class ClientManager {
    private final String LOG_FILE_PREFIX = "log/clientLog_";
    private final String LOG_FILE_SUFFIX = ".txt";

    private PrintStream fileOut = null;
    private final Config config = new Config();

    public ClientManager() {
        setLog();
    }


    public void runCommands(String commandFilePath) {
        final String FID = "ClientManager.runCommands()";

        try {
            ServerConn serverConn = ServerConnFactory.makeConn(config.getRandomServer());

            ArrayList<Command> commands = config.getCommands(commandFilePath);
            Log.info(FID, "Running " + commands.size() + " commands");

            while (!commands.isEmpty()) { 
                Command command = commands.remove(0);

                switch (command.getCommandType()) {
                    case READ:
                        handleRead(serverConn.getClient(), command.getFileId());
                        break;
                    case WRITE:
                        handleWrite(serverConn.getClient(), command.getFileId());
                        break;
                    case PRINT:
                        handlePrint(serverConn.getClient());
                        break;
                }
            }

            serverConn.close();

        } catch (TTransportException x) {
            Log.error(FID, "TTransportException", x);
        } 
    }


    private void handleRead(Server.Client client, int fileId) {
        final String FID = "ClientManager.handleRead()";
        try {
            ReadResponse readResponse = client.ClientRead(fileId);
        } catch (TException x) {
            Log.error(FID, "Error reading from server", x);
        }
    }


    private void handleWrite(Server.Client client, int fileId) {
        final String FID = "ClientManager.handleWrite()";
        try {
            WriteResponse writeResponse = client.ClientWrite(fileId);
        } catch (TException x) {
            Log.error(FID, "Error writing to server", x);
        }
    }


    private void handlePrint(Server.Client client) {
        final String FID = "ClientManager.handlePrint()";
        try {
            StructResponse structResponse = client.ClientGetStruct();
        } catch (TException x) {
            Log.error(FID, "Error getting struct of server", x);
        }
    }


    private void setLog() {
        final String FID = "ClientManager.setLog()";
        int num = 0;
        String fileName = LOG_FILE_PREFIX + num + LOG_FILE_SUFFIX;
        File file = new File(fileName);

        while (file.exists()) {
            num++;
            fileName = LOG_FILE_PREFIX + num + LOG_FILE_SUFFIX;
            file = new File(fileName);
        }

        System.out.println("All output directed to: " + fileName);

        try {
            fileOut = new PrintStream(fileName);
            System.setOut(fileOut);  
        } catch (FileNotFoundException x) {
            Log.error(FID, "Error: Client not able to establish a log file.");
        }
    }
    


    public void testCommands(String commandFilePath) {
        ArrayList<Command> commands = config.getCommands(commandFilePath);

        System.out.println("Printing Commands: ");
        for (Command command : commands) {
            Print.command(command);
        }
    }


    public void testConfig() {
        System.out.println("numFiles: " + config.getNumFiles());
        System.out.println("readQuorum: " + config.getReadQuorum());
        System.out.println("writeQuorum: " + config.getWriteQuorum());
        System.out.print("Coordinator: ");
        Print.serverInfo(config.getCoordinator());
        
        ServerInfo[] servers = config.getServers();

        System.out.println("Servers:");
        for (ServerInfo serverInfo : servers) {
            Print.serverInfo(serverInfo);
        }

        System.out.print("My ServerInfo: ");
        Print.serverInfo(config.getMyServerInfo());
    }
}
