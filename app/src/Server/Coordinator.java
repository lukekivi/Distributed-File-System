package Server;

import java.util.Random;
import pa3.WriteResponse;
import pa3.ReadResponse;
import pa3.File;
import pa3.Status;
import Data.ServerInfo;
import utils.Config;
import utils.Log;
import utils.RPC;
import utils.SemHelper;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class Coordinator {
    ServerManager manager;
    SemHelper sem;
    ArrayList<ServerInfo> servers;


    public coordinator(ServerManager manager) {
        this.manager = manager;
        this.sem = new SemHelper(manager.files.size());
        servers = manager.config.getServers();
    }


    /** 
    * Loops through a write quorum, connects, calls quorumWrite() on that server
    */
    public WriteResponse handleWrite(File file) {
        final String FID = "Coordinator.handleWrite()";
        ArrayList<ServerInfo> quorum = buildWriteQuorum(); // Build the quorum

        for (int i = 0; i < quorum.size(); i++) { // Iterate throug the quorum and write
            ServerInfo server = quorum.get(i);

            sem.wait(file.id); // Wait until this thread acquires lock for the specific file
            WriteResponse writeInfo = quorumWrite(file, server);
            if (writeInfo.status == Status.ERROR || writeInfo.Status == Status.NOT_FOUND) {
                Log.error(FID, "Coordinator write failed for file " + file.id + " to " + server.id);
            }
            sem.signal(file.id); // Give control back to next waiting process for the specific file
        }

        WriteResponse ans = new WriteResponse();
        ans.status = Status.SUCCESS;
        ans.msg = "Successfully updated file " + file.id + " in the write quorum";
        return ans;
    }


    /** 
    * Loops through a read quorum, connects, calls quorumRead() on that server
    */
    public ReadResponse handleRead(File file) {
        final String FID = "Coordinator.handleRead()";
        ArrayList<ServerInfo> readQuorum = buildReadQuorum(); // Build the quorum
        File highest = null;

        for (int i = 0; i < quorum.size(); i++) { // Iterate throug the quorum and read
            ServerInfo server = quorum.get(i);

            sem.wait(file.id); // Wait until this thread acquires lock for the specific file
            ReadResponse readInfo = quorumRead(file.id, server);
            if (readInfo.status == Status.ERROR || readInfo == Status.NOT_FOUND) {
                Log.error(FID, "Coordinator read failed for file " + file.id + " to " + server.id);
            }
            sem.signal(file.id); // Give control back to next waiting process for the specific file

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
        ans.msg = "Successfully read file " + file.id + " from the read quorum";
        return ans;
    }


    /** 
    * Loops through each server and grabs a folder that contains all files on that server
    */
    public StructResponse handleGetStruct() {
        final String FID = "Coordinator.handleGetStruct()";
        int size = manager.servers.size();
        StructResponse response = new StructResponse();
        ArrayList<Folder> folders = new ArrayList<Folder>();

        for (int i = 0; i < size; i++) {
            ServerInfo server = servers.get(i);
            // Establish connection to 'server'
            // FolderResponse folderResponse = Call CoordGetFolder()
            FolderResponse folderResponse = coordGetFolder(FID, server);

            if (folderResponse.status == SUCCESS) {
                folders.add(newFolder);
            } else {
                Log.error(FID, "Error when connecting to server " + server.id);
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
        if (server.id != manager.info.id) { // RPC call
            // Establish connection to `server`
            // response = Call CoordWrite();
            response = ServerComm.coordWrite(FID, server, file.id);

        } else { // Local
            response = new WriteResponse();
            response.status = manager.writeFile(file);
            response.msg = "Successfully updated file " + file.id + " for server " manager.info.id;
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

        if (server.id != manager.info.id) { // RPC call
            // Establish connection to `server`
            // ReadResponse response = Call CoordRead(fileId);
            response = ServerComm.coordRead(FID, server, fileId);

        } else { // Local call
            response = new ReadResponse();
            response.file = manager.readFile(fileId);
            if (response.file != null) {
                response.status = Status.SUCCESS;
                response.msg = "Successfully read file " + fileId + " from server " + manager.info.id;
            } else {
                response.status = Status.ERROR;
                response.msg = "Failed to read file " + fileId + " from server " + manager.info.id;
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
        ArrayList<ServerInfo> tempServers = manager.config.getServers(); // Going to be removing from this so can't use the coordinator's var

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
        ArrayList<ServerInfo> tempServers = manager.config.getServers();

        ArrayList<ServerInfo> quorum = new ArrayList<ServerInfo>();
        for (int i = 0; i < NR; i++) {
            int index = r.nextInt(tempServers.size());
            quorum.add(tempServers.get(index));
            tempServers.remove(index);
        }
        return quorum;
    }
}