package Server;

import pa3.Status;
import pa3.File;
import utils.Config;
import Data.ServerInfo;


public class ServerManager {
    public ServerInfo info;
    public Config config;
    public ArrayList<File> files;
    private PrintStream fileOut = null;
    private final String LOG_FILE = "log/server";

    public ServerManager(ServerInfo info, Config c) {
        this.info = info; // This contains info on the server
        config = c; // Access to config
        files = c.getFiles(); // All files in this server
    }


    public ArrayList<File> getStructure() {
        return files;
    }

    /** 
    * Takes fileId and returns the file that has fileId
    */
    public File readFile(int fileId) {
        for (int i = 0; i < files.size(); i++) {
            if (files[i].id == fileId) {
                return files[i];
            }
        }
        return null;
    }


    /** 
    * Loops for the file passed in and then sets it equal
    */
    public Status writeFile(File file) {
        for (int i = 0; i < files.size(); i++) {
            if (files[i].id == file.id) {
                files[i].version = file.version + 1; // Increment version
                return Status.SUCCESS;
            }
        }
        return Status.NOT_FOUND;
    }


    /** 
    * Returns if it's a coordinator or not
    */
    public ServerInfo getCoordinator() {
        return config.getCoordinator();
    }


    /** 
    * Sets up the server's log
    */
    public void setLog(int id)  {
        final String FID = "ServerManager.setLog()";
        try {
            if (id == -1) {
                fileOut = new PrintStream("log/coordLog.txt");
            } else {
                fileOut = new PrintStream(LOG_FILE + id + ".txt");
            }
            System.setOut(fileOut);  
        } catch (FileNotFoundException x) {
            Log.error(FID, "Error: Server " + info.id + " not able to establish a server file.", x);
        }
    }

    /** 
    * Close's the servers log
    */
    public void closeLog() {
        if (fileOut != null) {
            fileOut.close();
        }
    }
}