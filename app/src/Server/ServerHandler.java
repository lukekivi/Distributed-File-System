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
    private boolean isCoord;

    public ServerHandler(ServerManager manager, Coordinator coordinator) {
        this.manager = manager;
        this.coordinator = coordinator;
        if (manager.getCoordinator().getId() == manager.info.getId()) {
            isCoord = true;
        } else {
            isCoord = false;
        }
    }


    @Override
    public WriteResponse ClientWrite(int fileId) {
        final String FID = "ServerHandler.ClientWrite()";
        if (isCoord) { // Is the coordinator
            return coordinator.handleWrite(fileId);
        } else {
            ServerInfo coordInfo = manager.config.getCoordinator();

            // Connect to coordInfo
            // Call ServerWrite(file) on coordInfo
            return ServerComm.serverWrite(FID, coordInfo, fileId);
        }
    }


    @Override
    public WriteResponse ServerWrite(int fileId) { // Always called onto Coordinator
        final String FID = "ServerHandler.ServerWrite()";
        if (!(isCoord)) {
            Log.error(FID, "This is not a coordinator");
        }
        return coordinator.handleWrite(fileId);
    }


    @Override
    public WriteResponse CoordWrite(File file) {
        final String FID = "ServerHandler.CoordWrite()";
        WriteResponse ans = new WriteResponse();
        if (isCoord) {
            Log.error(FID, "This is a coordinator");
        }
        ans.status = manager.writeFile(file);
        if (ans.status == Status.SUCCESS) {
            ans.msg = "Successfully read file " + file.id;
        } else {
            ans.msg = "FAIL: Something went wrong, check log files";
        }
        return ans;
    }


    @Override
    public ReadResponse ClientRead(int fileId) {
        final String FID = "ServerHandler.ClientRead()";
        if (isCoord) {
            return coordinator.handleRead(fileId);
        } else {
            ServerInfo coordInfo = manager.config.getCoordinator();

            // Connect to coordInfo
            // Call ServerRead(file) on coordInfo
            return ServerComm.serverRead(FID, coordInfo, fileId);
        }
    }


    @Override
    public ReadResponse ServerRead(int fileId) {
        final String FID = "ServerHandler.ServerRead()";
        if (!(isCoord)) {
            Log.error(FID, "This is not a coordinator");
        }
        return coordinator.handleRead(fileId);
    }


    @Override
    public ReadResponse CoordRead(int fileId) {
        final String FID = "ServerHandler.CoordRead()";
        ReadResponse ans = new ReadResponse();
        if (isCoord) {
            Log.error(FID, "This is a coordinator");
        }
        ans.file = manager.readFile(fileId);
        if (ans.file != null) {
            ans.status = Status.SUCCESS;
            ans.msg = "Successfully read file " + fileId;
        } else {
            ans.status = Status.NOT_FOUND;
            ans.msg = "FAIL: Something went wrong, check log files";
        }
        return ans;
    }


    @Override
    public StructResponse ClientGetStruct() { // Client calls this on Server
        final String FID = "ServerHandler.ClientGetStruct()";
        if (isCoord) {
            return coordinator.handleGetStruct();
        } else {
            ServerInfo coordInfo = manager.config.getCoordinator();

            // Connect to coordInfo
            return ServerComm.serverGetStruct(FID, coordInfo);
        }
    }


    @Override
    public StructResponse ServerGetStruct() { // Server calls this on Coordinator
        final String FID = "ServerHandler.ServerGetStruct()";
        if (!(isCoord)) {
            Log.error(FID, "This is not a coordinator");
        }
        return coordinator.handleGetStruct();
    }


    @Override
    public FolderResponse CoordGetFolder() { // Coordinator calls this on each server
        FolderResponse response = new FolderResponse();

        Folder folder = new Folder();
        folder.files = manager.files;

        response.folder = folder;
        response.status = Status.SUCCESS;
        response.msg = "Successfully grabbed the folder for server " + manager.info.getId();

        return response;
    }

}