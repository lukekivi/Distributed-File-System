package server;

import pa3.Status;
import pa3.File;
import utils.Config;
import utils.Log;
import data.ServerInfo;
import java.io.PrintStream;
import java.util.ArrayList;
import java.io.FileNotFoundException;


public class ServerManager {
    public ServerInfo info; // Server info
    public Config config; // Access to config info
    public ArrayList<File> files; // List of the files on this server
    private PrintStream fileOut = null;
    private final String LOG_FILE = "log/server";

    public ServerManager(ServerInfo info, Config c) {
        final String FID = "ServerManager.ServerManager()";
        this.info = info;
        config = c;
        files = new ArrayList<File>();
        Status initStatus = initFiles(c.getNumFiles()); // Initialize all the files
        if (initStatus != Status.SUCCESS) {
            Log.error(FID, "File initialization went wrong.");
        }
    }


    /**
     * Initialize all of the files on this server
     * @param total total number of files to initialize
     * @return status of how the initialization went
     */
    public Status initFiles(int total) {
        for (int i = 0; i < total; i++) { // Create the right num of files
            File newFile = new File(); // Create the file
            newFile.id = i; // Id of the file
            newFile.version = 0; // All start at version 0, increment with each write call
            files.add(newFile); // Add to the list
        }
        return Status.SUCCESS;
    }


    /**
     * Return all of the files on this server
     * @return list of the files
     */
    public ArrayList<File> getStructure() {
        return files;
    }

    /**
     * Takes fileId and returns the file that has fileId
     * @param fileId the file's id that needs to be returned
     * @return the file that is requested
     */
    public File readFile(int fileId) {
        for (int i = 0; i < files.size(); i++) { // Loop through files to find the one requested
            File file = files.get(i);
            if (file.id == fileId) { // Found the file
                Log.info("SERVER: Read() of file " + fileId + " returned version " + file.version + ".");
                return file;
            }
        }
        Log.info("SERVER: Read() of file " + fileId + " FAILED.");
        return null;
    }


    /**
     * Loops for the file passed in and then sets it equal
     * @param file the updated file that should replace the existing one
     * @return status of how the write operation went
     */
    public Status writeFile(File file) {
        for (int i = 0; i < files.size(); i++) { // Loop through files to find the one requested
            File tempFile = files.get(i);
            if (tempFile.id == file.id) { // Found the file
                tempFile.version = file.version; // Increment version
                Log.info("SERVER: Write() of file " + file.id + " returned SUCCESSFULLY.");
                return Status.SUCCESS;
            }
        }
        Log.info("SERVER: Write() of file " + file.id + " FAILED.");
        return Status.NOT_FOUND;
    }


    /**
     * Get the coordinator information
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
                fileOut = new PrintStream("log/coordLog.txt"); // Setting the log file name for coordinator
            } else {
                fileOut = new PrintStream(LOG_FILE + id + ".txt"); // Setting the log file name for ordinary Server
            }
            System.setOut(fileOut);  
        } catch (FileNotFoundException x) {
            Log.error(FID, "Error: Server " + info.getId() + " not able to establish a server file.", x);
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