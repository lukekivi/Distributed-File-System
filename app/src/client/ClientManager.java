package client;

import utils.RPC.ServerConnFactory;
import utils.RPC.ServerConn;
import utils.Print;
import utils.Log;
import data.Command;
import data.ServerInfo;
import utils.Config;
import java.util.ArrayList;
import java.util.List;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.TException;
import pa3.Server;
import pa3.Status;
import data.CommandType;
import pa3.File;
import pa3.Folder;
import pa3.ReadResponse;
import pa3.StructResponse;
import pa3.WriteResponse;


public class ClientManager {
    private final Config config = new Config();

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
                    case CHECK:
                        handleCheck(serverConn.getClient());
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

            if (readResponse.status == Status.SUCCESS) {
                Log.info("Read File[" + readResponse.file.id + "] = " + readResponse.file.version);

            } else if (readResponse.status == Status.NOT_FOUND) {
                Log.info("Attempted to read File[" + fileId + "] but it was not found.");

            } else {
                Log.info("Attempted to read from File[" + fileId + "] but an error ocurred.\n\t- " + readResponse.msg);

            }

        } catch (TException x) {
            Log.error(FID, "Error reading from server", x);
        }
    }


    private void handleWrite(Server.Client client, int fileId) {
        final String FID = "ClientManager.handleWrite()";
        try {
            WriteResponse writeResponse = client.ClientWrite(fileId);

            if (writeResponse.status == Status.SUCCESS) {
                Log.info("Write to File[" + fileId + "] succeeded.");

            } else if (writeResponse.status == Status.NOT_FOUND) {
                Log.info("Attempted to write to File[" + fileId + "] but it was not found.");

            } else {
                Log.info("Attempted to write to file but an error ocurred.\n\t- " + writeResponse.msg);
            }

        } catch (TException x) {
            Log.error(FID, "Error writing to server", x);
        }
    }


    private void handlePrint(Server.Client client) {
        final String FID = "ClientManager.handlePrint()";
        try {
            StructResponse structResponse = client.ClientGetStruct();

            if (structResponse.status == Status.SUCCESS) {
                Log.info("File System Structure");

                for (Folder folder : structResponse.folders) {
                    Log.info("Folder[" + folder.serverId + "]");
                    for (File file : folder.files) {
                        Log.info("\t- File[" + file.id + "] = " + file.version);
                    }
                }

            } else {
                Log.info("Attempted get structure of file system but an occurred occurred.\n\t- " + structResponse.msg);
            } 

        } catch (TException x) {
            Log.error(FID, "Error getting struct of server", x);
        }
    }


    private void handleCheck(Server.Client client) {
        final String FID = "ClientManager.handleCheck()";
        try {
            StructResponse layout = client.ClientGetStruct();
            if (layout.status != Status.SUCCESS) {
                Log.error(FID, "Attempted get structure of file system but an error occurred.\n\t- " + layout.msg);
            }
            
            List<Folder> folders = layout.folders;
            int nW = config.getWriteQuorum(); // Get write quorum size to use as expected value
            int numFiles = config.getNumFiles();
            int numServers = config.getNumServers();
            int[][] fileVersions = new int[numFiles][numServers]; // Nested array, outer is files and inner is servers

            // Populate the nested array
            for (int i = 0; i < numFiles; i++) { // Iterate through each server's folder
                for (int j = 0; j < numServers; j++) { // Iterate through each file in the folder
                    Folder folder = folders.get(j);
                    File file = folder.files.get(i);
                    fileVersions[i][j] = file.version;
                }
            }

            for (int i = 0; i < numFiles; i++) {

                int highest = 0;
                for (int j = 0; j < numServers; j++) { // Find the highest version number
                    if (fileVersions[i][j] > highest) {
                        highest = fileVersions[i][j];
                    }
                }

                int counter = 0;
                for (int j = 0; j < numServers; j++) { // Check how many servers have the max version number
                    if (fileVersions[i][j] == highest) {
                        counter += 1;
                    }
                }
                Log.info(FID, "File " + i + ": Version " + highest + "\n\t" + counter + "/" + nW + " servers had this version number.");
            }
            

        } catch (TException x) {
            Log.error(FID, "Error writing to server", x);
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
