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


    public Coordinator(ServerManager manager) {
        this.manager = manager;
        this.sem = new SemHelper(manager.files.size());
        servers = manager.config.getServers();
    }


    /** 
    * Loops through a write quorum, connects, calls quorumWrite() on that server
    */
    public WriteResponse handleWrite(int fileId) {
        final String FID = "Coordinator.handleWrite()";
        ArrayList<ServerInfo> quorum = buildWriteQuorum(); // Build the quorum
        File highest = null;

        sem.wait(fileId); // Wait until this thread acquires lock for the specific file

        for (int i = 0; i < quorum.size(); i++) { // Iterate through quorum to find the highest version
            ServerInfo server = quorum.get(i);
            ReadResponse readInfo = quorumRead(fileId, server);

            if (readInfo.status != Status.SUCCESS) {
                Log.error(FID, "Coordinator read failed for file " + fileId + " to " + server.getId());
            }

            if (highest == null) {
                highest = readInfo.file;
            } else {
                if (readInfo.file.version > highest.version) {
                    highest = readInfo.file;
                }
            }
        }

        File newFile = new File(); // New file to overwrite the others with, version incremented
        newFile.version = highest.version + 1;
        newFile.id = highest.id;


        for (int i = 0; i < quorum.size(); i++) { // Iterate through the quorum and write
            ServerInfo server = quorum.get(i);

            WriteResponse writeInfo = quorumWrite(newFile, server);
            if (writeInfo.status == Status.ERROR || writeInfo.status == Status.NOT_FOUND) {
                Log.error(FID, "Coordinator write failed for file " + newFile.id + " to " + server.getId());
            }
        }

        sem.signal(newFile.id); // Give control back to next waiting process for the specific file

        WriteResponse ans = new WriteResponse();
        ans.status = Status.SUCCESS;
        ans.msg = "Successfully updated file " + newFile.id + " in the write quorum";

        return ans;
    }


    /** 
    * Loops through a read quorum, connects, calls quorumRead() on that server
    */
    public ReadResponse handleRead(int fileId) {
        final String FID = "Coordinator.handleRead()";
        ArrayList<ServerInfo> quorum = buildReadQuorum(); // Build the quorum
        File highest = null;

        for (int i = 0; i < quorum.size(); i++) { // Iterate throug the quorum and read
            ServerInfo server = quorum.get(i);

            sem.wait(fileId); // Wait until this thread acquires lock for the specific file
            ReadResponse readInfo = quorumRead(fileId, server);
            if (readInfo.status != Status.SUCCESS) {
                Log.error(FID, "Coordinator read failed for file " + fileId + " to " + server.getId());
            }
            sem.signal(fileId); // Give control back to next waiting process for the specific file

            if (highest == null) {
                highest = readInfo.file;
            } else {
                if (readInfo.file.version > highest.version) {
                    highest = readInfo.file;
                }
            }
        }

        ReadResponse ans = new ReadResponse();
        ans.file = highest;
        ans.status = Status.SUCCESS;
        ans.msg = "Successfully read file " + fileId + " from the read quorum";
        return ans;
    }


    /** 
    * Loops through each server and grabs a folder that contains all files on that server
    */
    public StructResponse handleGetStruct() {
        final String FID = "Coordinator.handleGetStruct()";
        int size = servers.length;
        StructResponse response = new StructResponse();
        ArrayList<Folder> folders = new ArrayList<Folder>();

        for (int i = 0; i < size; i++) {
            ServerInfo server = servers[i];
            FolderResponse folderResponse;
            // Establish connection to 'server'
            // FolderResponse folderResponse = Call CoordGetFolder()

            if (server.getId() == manager.info.getId()) { // Server is this one, don't initiate RPC call
                Folder folder = new Folder();
                folder.serverId = manager.info.getId();
                Log.info(FID, "Server id is " + folder.serverId);
                folder.files = manager.files;
                folders.add(folder);
            } else {
                folderResponse = ServerComm.coordGetFolder(FID, server);
                if (folderResponse.status == Status.SUCCESS) {
                    folders.add(folderResponse.folder);
                    Log.info(FID, "Server id is " + folderResponse.folder.serverId);
                } else {
                    Log.error(FID, "Error when connecting to server " + server.getId());
                }
            }
        }
        response.status = Status.SUCCESS;
        response.msg = "Successfully got each server's folder";
        
        return response;
    }


    /** 
    * Connects to the passed in server, calls CoordWrite() on that server
    */
    private WriteResponse quorumWrite(File file, ServerInfo server) {
        final String FID = "Coordinator.quorumWrite()";

        WriteResponse response;
        if (server.getId() != manager.info.getId()) { // RPC call
            // Establish connection to `server`
            // response = Call CoordWrite();
            response = ServerComm.coordWrite(FID, server, file);

        } else { // Local
            response = new WriteResponse();
            response.status = manager.writeFile(file);
            response.msg = "Successfully updated file " + file.id + " for server " + manager.info.getId();
        }
        return response;
    }


    /** 
    * Connects to the passed in server, calls CoordRead() on that server
    */
    private ReadResponse quorumRead(int fileId, ServerInfo server) {
        final String FID = "Coordinator.quorumRead()";
        File highest = null;
        ReadResponse response;

        if (server.getId() != manager.info.getId()) { // RPC call
            // Establish connection to `server`
            // ReadResponse response = Call CoordRead(fileId);
            response = ServerComm.coordRead(FID, server, fileId);

        } else { // Local call
            response = new ReadResponse();
            response.file = manager.readFile(fileId);
            if (response.file != null) {
                response.status = Status.SUCCESS;
                response.msg = "Successfully read file " + fileId + " from server " + manager.info.getId();
            } else {
                response.status = Status.ERROR;
                response.msg = "Failed to read file " + fileId + " from server " + manager.info.getId();
            }
        }
        return response;
    }


    /** 
    * Builds a quorum for write()
    */
    private ArrayList<ServerInfo> buildWriteQuorum() {
        final String FID = "Coordinator.buildWriteQuorum()";
        Random r = new Random();
        int NW = manager.config.getWriteQuorum();
        ArrayList<ServerInfo> tempServers = new ArrayList(Arrays.asList(servers)); // Going to be removing from this so can't use the coordinator's var

        ArrayList<ServerInfo> quorum = new ArrayList<ServerInfo>();
        for (int i = 0; i < NW; i++) {
            int index = r.nextInt(tempServers.size());
            quorum.add(tempServers.get(index));
            tempServers.remove(index);
        }
        return quorum;
    }


    /** 
    * Builds a quorum for read()
    */
    private ArrayList<ServerInfo> buildReadQuorum() {
        final String FID = "Coordinator.buildReadQuorum()";
        Random r = new Random();
        int NR = manager.config.getReadQuorum();
        ArrayList<ServerInfo> tempServers = new ArrayList(Arrays.asList(servers));

        ArrayList<ServerInfo> quorum = new ArrayList<ServerInfo>();
        for (int i = 0; i < NR; i++) {
            int index = r.nextInt(tempServers.size());
            quorum.add(tempServers.get(index));
            tempServers.remove(index);
        }
        return quorum;
    }
}