package utils.RPC;

import pa3.ServerInfo;
import pa3.Server;
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
    private static final ServerConnFactory factory = new ServerConnFactory();
}