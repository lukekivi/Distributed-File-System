package server;

import java.util.Random;
import pa3.WriteResponse;
import pa3.ReadResponse;
import pa3.StructResponse;
import pa3.FolderResponse;
import pa3.File;
import pa3.Status;
import pa3.Folder;
import data.ServerInfo;
import utils.Config;
import utils.Log;
import utils.SemHelper;
import utils.RPC.ServerComm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;


public class Coordinator {
    ServerManager manager;
    SemHelper sem;
    ServerInfo[] servers;
    int numFiles;


    public Coordinator(ServerManager manager) {
        this.manager = manager;
        this.sem = new SemHelper(manager.files.size()); // Semaphores for each file
        servers = manager.config.getServers(); // List of servers
        numFiles = manager.files.size(); // Number of files on each server
    }


    /**
     * Loops through a write quorum, connects, calls quorumWrite() on that server
     * @param fileId the file id
     * @return Outcome/response of the write
     */
    public WriteResponse handleWrite(int fileId) {
        final String FID = "Coordinator.handleWrite()";
        WriteResponse ans = new WriteResponse();

        if (fileId >= numFiles) { // File passed in is not valid
            ans.status = Status.NOT_FOUND;
            ans.msg = "Input file " + fileId + " not found.";
            return ans;
        }

        ArrayList<ServerInfo> quorum = buildWriteQuorum(); // Build the quorum
        File highest = null;

        sem.wait(fileId); // Wait until this thread acquires lock for the specific file

        for (int i = 0; i < quorum.size(); i++) { // Iterate through quorum to find the highest version of the file first
            ServerInfo server = quorum.get(i);
            ReadResponse readInfo = quorumRead(fileId, server); // Read helper for the quorum

            if (readInfo.status != Status.SUCCESS) { // Something went wrong
                Log.error(FID, "Coordinator read failed for file " + fileId + " to " + server.getId());
            }

            if (highest == null) { // Can't compare a null object
                highest = readInfo.file;
            } else {
                if (readInfo.file.version > highest.version) {
                    highest = readInfo.file; // Found a new highest version
                }
            }
        }

        File newFile = new File(); // New file to overwrite the others with, version incremented
        newFile.version = highest.version + 1;
        newFile.id = highest.id;


        for (int i = 0; i < quorum.size(); i++) { // Iterate through the quorum and write
            ServerInfo server = quorum.get(i);

            WriteResponse writeInfo = quorumWrite(newFile, server); // Write helper for the quorum
            if (writeInfo.status != Status.SUCCESS) { // Something went wrong with the write
                Log.error(FID, "Coordinator write failed for file " + newFile.id + " to " + server.getId());
            }
        }

        sem.signal(newFile.id); // Give control back to next waiting process for the specific file

        ans.status = Status.SUCCESS;
        ans.msg = "Successfully updated file " + newFile.id + " in the write quorum";

        return ans;
    }


    /**
     * Loops through a read quorum, connects, calls quorumRead() on that server
     * @param fileId the file id
     * @return Outcome/response of the read
     */
    public ReadResponse handleRead(int fileId) {
        final String FID = "Coordinator.handleRead()";
        ReadResponse ans = new ReadResponse();

        if (fileId >= numFiles) { // File passed in is not valid
            ans.file = null;
            ans.status = Status.NOT_FOUND;
            ans.msg = "Input file " + fileId + " not found.";
            return ans;
        }

        ArrayList<ServerInfo> quorum = buildReadQuorum(); // Build the quorum
        File highest = null;

        for (int i = 0; i < quorum.size(); i++) { // Iterate throug the quorum and read
            ServerInfo server = quorum.get(i);

            sem.wait(fileId); // Wait until this thread acquires lock for the specific file
            ReadResponse readInfo = quorumRead(fileId, server); // Read helper for the quorum
            if (readInfo.status != Status.SUCCESS) { // Something went wrong in the read call
                Log.error(FID, "Coordinator read failed for file " + fileId + " to " + server.getId());
            }
            sem.signal(fileId); // Give control back to next waiting process for the specific file

            if (highest == null) { // Can't compare nulls
                highest = readInfo.file;
            } else {
                if (readInfo.file.version > highest.version) {
                    highest = readInfo.file; // Found a higher version of the file
                }
            }
        }

        ans.file = highest;
        ans.status = Status.SUCCESS;
        ans.msg = "Successfully read file " + fileId + " from the read quorum";
        return ans;
    }


    /**
     * Loops through each server and grabs a folder that contains all files on that server
     * @return struct holding the folders for each server that contains all of the files
     */
    public StructResponse handleGetStruct() {
        final String FID = "Coordinator.handleGetStruct()";
        int size = servers.length; // Number of servers
        StructResponse response = new StructResponse();
        ArrayList<Folder> folders = new ArrayList<Folder>();

        for (int i = 0; i < size; i++) {
            ServerInfo server = servers[i];
            FolderResponse folderResponse;

            if (server.getId() == manager.info.getId()) { // Server is this one, don't initiate RPC call
                Folder folder = new Folder(); // Create a folder
                folder.serverId = manager.info.getId();
                folder.files = manager.files; // Grab files
                folders.add(folder); // Add server's folder to list of folders
            } else { // Inititate RPC call on the currently looped server
                folderResponse = ServerComm.coordGetFolder(FID, server);
                if (folderResponse.status == Status.SUCCESS) { // Check statement to make sure the call worked
                    folders.add(folderResponse.folder);
                } else {
                    Log.error(FID, "Error when connecting to server " + server.getId());
                }
            }
        }
        response.status = Status.SUCCESS;
        response.msg = "Successfully got each server's folder";
        response.folders = folders;
        
        return response;
    }


    /**
     * Connects to the passed in server, calls CoordWrite() on that server
     * @param file the file that the others should be overwritten with
     * @param server the server that needs its file overwritten
     * @return Outcome/response of the write
     */
    private WriteResponse quorumWrite(File file, ServerInfo server) {
        final String FID = "Coordinator.quorumWrite()";

        WriteResponse response;
        if (server.getId() != manager.info.getId()) { // RPC call on server passed in
            response = ServerComm.coordWrite(FID, server, file);

        } else { // Local
            response = new WriteResponse();
            response.status = manager.writeFile(file); // Write the file
            response.msg = "Successfully updated file " + file.id + " for server " + manager.info.getId();
        }
        return response;
    }


    /**
     * Connects to the passed in server, calls CoordRead() on that server
     * @param fileId the file id
     * @param server the server that the read is to be called on
     * @return Outcome/response of the read
     */
    private ReadResponse quorumRead(int fileId, ServerInfo server) {
        final String FID = "Coordinator.quorumRead()";
        File highest = null;
        ReadResponse response;

        if (server.getId() != manager.info.getId()) { // RPC call
            response = ServerComm.coordRead(FID, server, fileId); // Call coordRead on the server passed in

        } else { // Local call
            response = new ReadResponse();
            response.file = manager.readFile(fileId);
            if (response.file != null) { // File was found
                response.status = Status.SUCCESS;
                response.msg = "Successfully read file " + fileId + " from server " + manager.info.getId();
            } else { // File wasn't found
                response.status = Status.ERROR;
                response.msg = "Failed to read file " + fileId + " from server " + manager.info.getId();
            }
        }
        return response;
    }


    /**
     * Builds a quorum for write()
     * @return writequorum which is a list of servers that are randomly chosen
     */
    private ArrayList<ServerInfo> buildWriteQuorum() {
        final String FID = "Coordinator.buildWriteQuorum()";
        Random r = new Random();
        int nW = manager.config.getWriteQuorum(); // Size of the write quorum
        ArrayList<ServerInfo> tempServers = new ArrayList(Arrays.asList(servers)); // Create a new list of servers so we can remove the servers we choose

        ArrayList<ServerInfo> quorum = new ArrayList<ServerInfo>(); // The quorum
        for (int i = 0; i < nW; i++) {
            int index = r.nextInt(tempServers.size()); // Get a random server from the list
            quorum.add(tempServers.get(index)); // Add it to the quorum
            tempServers.remove(index); // Remove the chosen server from the pool of available ones
        }
        return quorum;
    }


    /**
     * Builds a quorum for read()
     * @return writequorum which is a list of servers that are randomly chosen
     */
    private ArrayList<ServerInfo> buildReadQuorum() {
        final String FID = "Coordinator.buildReadQuorum()";
        Random r = new Random();
        int NR = manager.config.getReadQuorum(); // Size of the read quorum
        ArrayList<ServerInfo> tempServers = new ArrayList(Arrays.asList(servers)); // Create a new list of servers so we can remove the servers we choose

        ArrayList<ServerInfo> quorum = new ArrayList<ServerInfo>(); // The quorum
        for (int i = 0; i < NR; i++) {
            int index = r.nextInt(tempServers.size()); // Get a random server from the list
            quorum.add(tempServers.get(index)); // Add it to the quorum
            tempServers.remove(index); // Remove the chosen server from the pool of available ones
        }
        return quorum;
    }
}