package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import utils.Log;

public class Client {
    private static final String RANDOM = "random";
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
         * They can also tell the system to randomize all
         * commands if they so desire.
         */
        if (args.length != 2) {
            Log.error(FID, "A command file path and random status are required as arguments. You provided " + args.length + " arguments. Random status can be either \"random\" or \"non-random\"");
        }

        boolean isRandom = RANDOM.equals(args[1]);

        String commandFilePath = args[0];

        long startTime = System.nanoTime();                 // start timer
        manager.runCommands(commandFilePath, isRandom);     // run commands
        long endTime   = System.nanoTime();                 // finish timer
        long totalTime = endTime - startTime;
        Log.info("Commands completed in " + totalTime + " nano seconds.");
    }


    /**
     * Set the log for this client. The log format is:
     * clientLog_<n> where n is the next available log id.
     */
    private static void setLog() {
        final String FID = "ClientManager.setLog()";
        int num = 0;
        String fileName = LOG_FILE_PREFIX + num + LOG_FILE_SUFFIX;  // start with clientLog_0.txt
        File file = new File(fileName);

        while (file.exists()) {
            num++;
            fileName = LOG_FILE_PREFIX + num + LOG_FILE_SUFFIX;     // increment until a file doesn't exist
            file = new File(fileName);
        }

        System.out.println("All output directed to: " + fileName);  // notify user of the log file name

        try {
            fileOut = new PrintStream(fileName);
            System.setOut(fileOut);  
        } catch (FileNotFoundException x) {
            Log.error(FID, "Error: Client not able to establish a log file.");
        }
    }
    
}
