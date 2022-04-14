package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import utils.Log;

public class Client {
    private static final String LOG_FILE_PREFIX = "log/clientLog_";
    private static final String LOG_FILE_SUFFIX = ".txt";

    private static PrintStream fileOut = null;
    private static final ClientManager manager = new ClientManager();
    public static void main(String[] args) {
        final String FID = "Client.Main()";

        Logger.getRootLogger().setLevel(Level.ERROR);

        setLog();

        /**
         * Users can provide a file with commands for the
         * client to complete.
         */
        if (args.length != 1) {
            Log.error(FID, "A command file path is required as an argument. You provided " + args.length + " arguments.");
        }

        String commandFilePath = args[0];

        long startTime = System.nanoTime();
        // manager.testConfig();
        manager.runCommands(commandFilePath);
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        Log.info("Commands completed in " + totalTime + " seconds.");
    }

    private static void setLog() {
        final String FID = "ClientManager.setLog()";
        int num = 0;
        String fileName = LOG_FILE_PREFIX + num + LOG_FILE_SUFFIX;
        File file = new File(fileName);

        while (file.exists()) {
            num++;
            fileName = LOG_FILE_PREFIX + num + LOG_FILE_SUFFIX;
            file = new File(fileName);
        }

        System.out.println("All output directed to: " + fileName);

        try {
            fileOut = new PrintStream(fileName);
            System.setOut(fileOut);  
        } catch (FileNotFoundException x) {
            Log.error(FID, "Error: Client not able to establish a log file.");
        }
    }
    
}
