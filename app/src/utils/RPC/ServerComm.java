package utils.RPC;

import data.ServerInfo;
import pa3.Folder;
import pa3.FolderResponse;
import pa3.ReadResponse;
import pa3.Server;
import pa3.WriteResponse;
import pa3.StructResponse;
import utils.Log;

import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.TException;


/**
 * ServerComm is a class that conveniently handles sever communication. It
 * essentially just abstracts away thrift boiler plate and truly allows 
 * users to treat RPC calls like simple function calls.
 * 
 * It creates thrift connections between system entites via the ServerConnFactory.
 * Then it calls RPC functions and handles errors. Each function gets a 
 * [from] field which helps build better error messages. [from] is supposed to be
 * of the form ClassName.functionName().
 */
public class ServerComm {
    public static WriteResponse serverWrite(String from, ServerInfo serverInfo, int fileId) {
        final String FID = "ServerComm.serverWrite()";
        WriteResponse writeResponse = null;
        try {
            ServerConn serverConn = ServerConnFactory.makeConn(serverInfo);
            writeResponse = serverConn.getClient().ServerWrite(fileId);

            serverConn.close();
        } catch (TTransportException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        } catch (TException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        }
        return writeResponse;
    }


    public static WriteResponse coordWrite(String from, ServerInfo serverInfo, int fileId) {
        final String FID = "ServerComm.coordWrite()";
        WriteResponse writeResponse = null;
        try {
            ServerConn serverConn = ServerConnFactory.makeConn(serverInfo);
            writeResponse = serverConn.getClient().CoordWrite(fileId);

            serverConn.close();
        } catch (TTransportException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        } catch (TException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        }
        return writeResponse;
    }


    public static ReadResponse serverRead(String from, ServerInfo serverInfo, int fileId) {
        final String FID = "ServerComm.serverRead()";
        ReadResponse readResponse = null;
        try {
            ServerConn serverConn = ServerConnFactory.makeConn(serverInfo);
            readResponse = serverConn.getClient().ServerRead(fileId);

            serverConn.close();
        } catch (TTransportException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        } catch (TException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        }
        return readResponse;
    }


    public static ReadResponse coordRead(String from, ServerInfo serverInfo, int fileId) {
        final String FID = "ServerComm.coordRead()";
        ReadResponse readResponse = null;
        try {
            ServerConn serverConn = ServerConnFactory.makeConn(serverInfo);
            readResponse = serverConn.getClient().CoordRead(fileId);

            serverConn.close();
        } catch (TTransportException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        } catch (TException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        }
        return readResponse;
    }


    public static StructResponse serverGetStruct(String from, ServerInfo serverInfo) {
        final String FID = "ServerComm.serverGetStruct()";
        StructResponse structResponse = null;
        try {
            ServerConn serverConn = ServerConnFactory.makeConn(serverInfo);
            structResponse = serverConn.getClient().ServerGetStruct();

            serverConn.close();
        } catch (TTransportException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        } catch (TException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        }
        return structResponse;
    }


    public static FolderResponse coordGetFolder(String from, ServerInfo serverInfo) {
        final String FID = "ServerComm.coordGetFolder()";
        FolderResponse folderResponse = null;
        try {
            ServerConn serverConn = ServerConnFactory.makeConn(serverInfo);
            folderResponse = serverConn.getClient().CoordGetFolder();

            serverConn.close();
        } catch (TTransportException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        } catch (TException x) {
            Log.error(from, "calling " + FID + " onto server " + serverInfo.getId(), x);
        }
        return folderResponse;
    }
}