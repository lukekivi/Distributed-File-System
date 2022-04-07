package utils.RPC;

import org.apache.thrift.transport.TTransport;
import pa3.Server;
import utils.Log;

/**
 * A data class to encapsulate server connection components
 */
public class ServerConn {
    final private String TAG = "ServerConn";
    private TTransport transport;
    private Server.Client client;

    public ServerConn(Server.Client client, TTransport transport) {
        if (!transport.isOpen()) {
            Log.error(TAG + "()", "passed transport was not opened.");
        }
        this.client = client;
        this.transport = transport;
    }

    public Server.Client getClient() {
        if (!transport.isOpen()) {
            Log.error(TAG + "getClient()", "transport was closeed.");
        }
        return client;
    }

    public void close() {
        transport.close();
    }
}
