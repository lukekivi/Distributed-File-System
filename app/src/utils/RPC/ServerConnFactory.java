package utils.RPC;

import pa3.Server;
import data.ServerInfo;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

/**
 * This is a factory that produces thrift connections via ServerInfo
 * data structures. It returns active serverConn objects.
 */
public class ServerConnFactory {
   /*
    * Return an active server connection. 
    */
    public static ServerConn makeConn(ServerInfo serverInfo) throws TTransportException {
        TTransport transport = new TSocket(serverInfo.getIp(), serverInfo.getPort());
        transport.open();

        TProtocol protocol = new  TBinaryProtocol(transport);
        Server.Client client = new Server.Client(protocol);

        return new ServerConn(client, transport);
   }
}
