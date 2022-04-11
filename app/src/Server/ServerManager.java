package Server;

import pa3.Status;
import pa3.File;
import utils.Config;
import pa3.ServerInfo;


public class ServerManager {
    public ServerInfo info;
    public Config config;
    public ArrayList<File> files;
    private PrintStream fileOut = null;
    private final String LOG_FILE = "log/server";

    public ServerManager(ServerInfo info, Config c) {
        this.info = info;
        config = c;
        files = c.getFiles();
    }


    public ArrayList<File> getStructure() {
        return files;
    }


    public File readFile(int fileId) {
        for (int i = 0; i < files.size(); i++) {
            if (files[i].id == fileId) {
                return files[i];
            }
        }
        return null;
    }


    public Status writeFile(File file) {
        for (int i = 0; i < files.size(); i++) {
            if (files[i].id == file.id) {
                files[i].version = file.version;
                return Status.SUCCESS;
            }
        }
        return Status.NOT_FOUND;
    }


    public ServerInfo getCoordinator() {
        return config.getCoordinator();
    }


    public void setLog(int id)  {
        try {
            if (id == -1) {
                fileOut = new PrintStream("log/coordLog.txt");
            } else {
                fileOut = new PrintStream(LOG_FILE + id + ".txt");
            }
            System.setOut(fileOut);  
        } catch (FileNotFoundException x) {
            System.out.println("Error: Server " + info.id + " not able to establish a server file.");
            System.exit(1);
        }
    }


    public void closeLog() {
        if (fileOut != null) {
            fileOut.close();
        }
    }
}