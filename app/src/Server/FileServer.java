package Server;

import data.ServerInfo;
import utils.Log;

import pa3.Status;
import Data.ServerInfo;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import java.io.FileNotFoundException;
import java.net.InetAddress;

public class FileServer {
    private static Config c;
    private static ServerManager manager;
    public static void main(String[] args) {
        final String FID = "FileServer.main()";
        try {
            Logger.getRootLogger().setLevel(Level.ERROR);

            c = new Config();
            String ip = InetAddress.getLocalHost().getHostName() + ".cselabs.umn.edu";
            ServerInfo serverInfo = c.getServerInfo(ip); // Get server info
            if (info == null) {
                Log.error(FID, "Server not found in config file");
            }

            manager = new ServerManager(manager, c);
            manager.setLog(serverInfo.id); // Setting the log for this server

            ServerHandler handler = null; // Create handler
            Coordinator coordinator = null;

            if (serverInfo.isCoord()) { // This is coordinator
                manager.setLog(serverInfo.id); // Setting the log for the coordinator
                coordinator = new Coordinator(manager);
                handler = new ServerHandler(null, coordinator);
            } else { // This is not coordinator
                manager.setLog(serverInfo.id); // Setting the log for this server
                handler = new ServerHandler(manager, null);
            }

            // Perform server duties
            Server.Processor processor = new Server.Processor<ServerHandler>(handler);
            Runnable simple = new Runnable() {
                public void run() {
                    multiThreadedServer(processor, serverInfo.port);
                }
            };
            new Thread(simple).start();

        } catch (Exception e) {
            Log.error(FID, "Something went wrong with seting up the server stuff", e);
        }
    }


    /**
     * Start a multiThreaded thrift server. 
     */
    public static void multiThreadedServer(Server.Processor processor, int port) {
        final String FID = "FileServer.multiThreadedServer()";
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            TThreadPoolServer server = new TThreadPoolServer( // Auto creates new threads for each task
                new TThreadPoolServer.Args(serverTransport).processor(processor)
            );

            Log.info(FID, "Starting the multi-threaded FileServer...");
            server.serve();
        } catch (Exception e) {
            Log.error(FID, "FileServer: Client connection closed with exception.", e);
        }

        Log.info(FID, "Closed the stream");
        manager.closeLog();
    }
}