package server;

import pa3.Server;
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
import utils.RPC.ServerComm;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.TException;

public class ServerHandler implements Server.Iface {
    private ServerManager manager;
    private Coordinator coordinator;
    private boolean isCoord; // True if coordinator, false if ordinary server

    public ServerHandler(ServerManager manager) { // Called if ordinary server
        this.manager = manager;
        this.coordinator = null;
        isCoord = false;
    }

    public ServerHandler(Coordinator coordinator) { // Called if a coordinator
        this.manager = null;
        this.coordinator = coordinator;
        isCoord = true;
    }


    /**
     * Sends a write request to a random server/coordinator
     * @param fileId the file id of the write call
     * @return Outcome/response of the write request
     */
    @Override
    public WriteResponse ClientWrite(int fileId) { // Always called by a Client onto a Server/Coordinator
        final String FID = "ServerHandler.ClientWrite()";
        if (isCoord) { // Is the coordinator
            return coordinator.handleWrite(fileId); // Start write call, this is coordinator
        } else {
            ServerInfo coordInfo = manager.config.getCoordinator();
            return ServerComm.serverWrite(FID, coordInfo, fileId); // Connect to coordinator and call serverWrite()
        }
    }


    /**
     * Sends a write request to the coordinator
     * @param fileId the file id of the write call
     * @return Outcome/response of the write request
     */
    @Override
    public WriteResponse ServerWrite(int fileId) { // Always called by a Server onto a Coordinator
        final String FID = "ServerHandler.ServerWrite()";
        if (!(isCoord)) {
            Log.error(FID, "This is not a coordinator");
        }
        return coordinator.handleWrite(fileId); // Handle the write call
    }


    /**
     * Calls the write call of a file onto a server
     * @param fileId the file id of the write call
     * @return Outcome/response of the write request
     */
    @Override
    public WriteResponse CoordWrite(File file) { // Always called by a Coordinator onto a Server
        final String FID = "ServerHandler.CoordWrite()";
        WriteResponse ans = new WriteResponse();
        if (isCoord) {
            Log.error(FID, "This is a coordinator");
        }
        ans.status = manager.writeFile(file); // Conduct write request on the file on this server
        if (ans.status == Status.SUCCESS) {
            ans.msg = "Successful Write() of file " + file.id + " on Server " + manager.info.getId() + " returned SUCCESSFULLY.";
        } else {
            ans.msg = "FAILED Write() of file " + file.id + " on Server " + manager.info.getId() + ".";
        }
        return ans;
    }


    /**
     * Sends a read request to a random server/coordinator
     * @param fileId the file id of the read call
     * @return Outcome/response of the read request
     */
    @Override
    public ReadResponse ClientRead(int fileId) { // Always called by a Client onto a Server/Coordinator
        final String FID = "ServerHandler.ClientRead()";
        if (isCoord) {
            return coordinator.handleRead(fileId); // Start write call, this is coordinator
        } else {
            ServerInfo coordInfo = manager.config.getCoordinator();
            return ServerComm.serverRead(FID, coordInfo, fileId); // Connect to coordinator and call serverRead()
        }
    }


    /**
     * Sends a read request to the coordinator
     * @param fileId the file id of the read call
     * @return Outcome/response of the read request
     */
    @Override
    public ReadResponse ServerRead(int fileId) { // Always called by a Server onto a Coordinator
        final String FID = "ServerHandler.ServerRead()";
        if (!(isCoord)) {
            Log.error(FID, "This is not a coordinator");
        }
        return coordinator.handleRead(fileId); // Handle the read call
    }


    /**
     * Calls the read call of a file onto a server
     * @param fileId the file id of the read call
     * @return Outcome/response of the read request
     */
    @Override
    public ReadResponse CoordRead(int fileId) { // Always called by a Coordinator onto a Server
        final String FID = "ServerHandler.CoordRead()";
        ReadResponse ans = new ReadResponse();
        if (isCoord) {
            Log.error(FID, "This is a coordinator");
        }
        ans.file = manager.readFile(fileId); // Conduct read request on the file on this server
        if (ans.file != null) {
            ans.status = Status.SUCCESS;
            ans.msg = "Successful Read() of file " + fileId + " on Server " + manager.info.getId() + " returned version " + ans.file.version + ".";
        } else {
            ans.status = Status.NOT_FOUND;
            ans.msg = "FAILED Read() of file " + fileId + " on Server " + manager.info.getId() + ".";
        }
        return ans;
    }


    /**
     * Sends a request for the servers' structures to a random server/coordinator
     * @return Outcome/response of the structure request
     */
    @Override
    public StructResponse ClientGetStruct() { // Always called by a Client onto a Server/Coordinator
        final String FID = "ServerHandler.ClientGetStruct()";
        if (isCoord) {
            return coordinator.handleGetStruct(); // Start structure request, this is coordinator
        } else {
            ServerInfo coordInfo = manager.config.getCoordinator();

            // Connect to coordInfo
            return ServerComm.serverGetStruct(FID, coordInfo); // Connect to coordinator and call serverGetStruct()
        }
    }


    /**
     * Sends a request for the servers' structures to the coordinator
     * @return Outcome/response of the structure request
     */
    @Override
    public StructResponse ServerGetStruct() { // Always called by a Server onto a Coordinator
        final String FID = "ServerHandler.ServerGetStruct()";
        if (!(isCoord)) {
            Log.error(FID, "This is not a coordinator");
        }
        StructResponse response = coordinator.handleGetStruct(); // Handle the structure request
        return response;
    }


    /**
     * Grabs the folder of a server which contains all of the files
     * @return The server's folder and the response
     */
    @Override
    public FolderResponse CoordGetFolder() { // Always called by a Coordinator onto a Server
        final String FID = "ServerHandler.CoordGetFolder()";
        if (isCoord) {
            Log.error(FID, "This is a coordinator");
        }
        FolderResponse response = new FolderResponse();

        Folder folder = new Folder(); // Create a folder
        folder.files = manager.files; // Add the files
        folder.serverId = manager.info.getId(); // Mark it with the server id

        response.folder = folder;
        response.status = Status.SUCCESS;
        response.msg = "Successfully grabbed the folder for server " + manager.info.getId();

        return response;
    }

}