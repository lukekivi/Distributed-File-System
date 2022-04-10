package data;

public class ServerInfo {
    public ServerInfo(int id, String ip, int port, boolean isCoord) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.isCoord = isCoord;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isCoord() {
        return isCoord;
    }


    private int id;
    private String ip;
    private int port;
    private boolean isCoord;
}