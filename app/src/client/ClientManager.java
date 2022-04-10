package client;

import utils.Print;
import utils.Log;
import data.ServerInfo;
import utils.Config;

public class ClientManager {
    private final Config config = new Config();

    public ClientManager() {
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
