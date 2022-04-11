package client;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import utils.Log;

public class Client {
    private static final ClientManager manager = new ClientManager();
    public static void main(String[] args) {
        final String FID = "Client.Main()";

        Logger.getRootLogger().setLevel(Level.ERROR);

        /**
         * Users can provide a file with commands for the
         * client to complete.
         */
        if (args.length != 1) {
            Log.error(FID, "A command file path is required as an argument. You provided " + args.length + " arguments.");
        }

        String commandFilePath = args[0];

        long startTime = System.nanoTime();
        manager.testConfig();
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        Log.info("Commands completed in " + totalTime + " seconds.");
        // manager.runCommands(commandFilePath);
    }
    
}
