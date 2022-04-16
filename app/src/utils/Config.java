package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import data.ServerInfo;
import data.Command;
import data.CommandType;

import java.util.*;
import utils.Log;
import java.net.InetAddress;

public class Config {    
    final private String CONFIG_FILE_PATH = "config/config.txt";
    final private String MACHINE_FILE_PATH = "config/machines.txt";
    final private int NUM_CONFIG_FIELDS = 4;
    final private int NUM_MACHINE_LINE_ARGS = 4;
    final private int WRITE_COMMAND_ARGS = 2;
    final private int READ_COMMAND_ARGS = 2;
    final private int PRINT_COMMAND_ARGS = 1;
    final private int CHECK_COMMAND_ARGS = 1;

    final private Random r = new Random();

    private int numServers;
    private int writeQuorum;
    private int readQuorum;
    private int numFiles;
    private ServerInfo[] servers;
    private ServerInfo coordinator;

    public Config() {
        readConfig();
        checkConfig();
        readMachines();
    }  


    public ServerInfo getRandomServer() {
        return servers[r.nextInt(numServers-1)];
    }


    public ServerInfo getCoordinator() {
        return coordinator;
    }

    
    public ServerInfo[] getServers() {
        return servers;
    }


    public int getNumServers() {
        return servers.length;
    }


    public int getReadQuorum() {
        return readQuorum;
    }


    public int getWriteQuorum() {
        return writeQuorum;
    }


    public int getNumFiles() {
        return numFiles;
    }


    /**
     * A server must configure itself and it needs its details
     * to do that. Compare the system's hostname with the ones
     * listed in machines.txt. 
     */
    public ServerInfo getMyServerInfo() {
        final String FID = "Config.getServerInfo()";

        try {
            String ip = InetAddress.getLocalHost().getHostName() + ".cselabs.umn.edu";;

            for (ServerInfo serverInfo : servers) {
                if (serverInfo.getIp().equals(ip)) {
                    return serverInfo;
                }
            }

            Log.error(FID, "Didn't find ip \"" + ip + "\" in list of servers.");
        } catch (Exception x) {
            Log.error(FID, "Error getting own IP address", x);
        }
        return null;
    }


    /**
     * Read all commands from the commandFilePath and return them in the 
     * form of an ArrayList
     */
    public ArrayList<Command> getCommands(String commandFilePath) {
        final String FID = "Config.getCommands()";
        try {
            FileInputStream file = new FileInputStream(commandFilePath);
            Scanner scanConfig = new Scanner(file);
            ArrayList<Command> commands = new ArrayList<Command>();
            String[] line;

            while (scanConfig.hasNextLine()) {
                line = scanConfig.nextLine().split(" ");
                if (line[0].equals("write")) {
                    if (line.length != WRITE_COMMAND_ARGS) {
                        Log.error(FID, "Write command requires " + (WRITE_COMMAND_ARGS - 1) + " args.");   
                    }
                    int fileId = Integer.parseInt(line[1]);
                    commands.add(new Command(CommandType.WRITE, fileId));
                } else if (line[0].equals("read")) {
                    if (line.length != READ_COMMAND_ARGS) {
                        Log.error(FID, "Read command requires " + (READ_COMMAND_ARGS - 1) + " args.");   
                    }
                    int fileId = Integer.parseInt(line[1]);
                    commands.add(new Command(CommandType.READ, fileId));
                } else if (line[0].equals("print")) {
                    if (line.length != PRINT_COMMAND_ARGS) {
                        Log.error(FID, "Print command requires " + (PRINT_COMMAND_ARGS - 1) + " args.");   
                    }
                    int fileId = -1;
                    commands.add(new Command(CommandType.PRINT, fileId));
                } else if (line[0].equals("check")) {
                    if (line.length != CHECK_COMMAND_ARGS) {
                        Log.error(FID, "Check command requires " + (CHECK_COMMAND_ARGS - 1) + " args.");   
                    }
                    int fileId = -1;
                    commands.add(new Command(CommandType.CHECK, fileId));
                }
            }

            scanConfig.close();
            return commands;
        } catch (Exception exception) {
            Log.error(FID, "Error reading file", exception);
        }
        return null;
    }


    /**
     * Read the configuration values from the config file
     * - num servers
     * - read quorum size
     * - write quorum size
     * - num files
     */
    private void readConfig() {
        final String FID = "Config.readConfig()";

        int flag = 0; // used to make sure everything was set
        try {
            FileInputStream file = new FileInputStream(CONFIG_FILE_PATH);
            Scanner scanConfig = new Scanner(file);
            String[] line;

            while (scanConfig.hasNextLine()) {
                line = scanConfig.nextLine().split(" ");
                if (line[0].equals("servers")) {
                    numServers = Integer.parseInt(line[1]);
                    servers = new ServerInfo[numServers];
                    flag++;
                } else if (line[0].equals("read")) {
                    readQuorum = Integer.parseInt(line[1]);
                    flag++;
                } else if (line[0].equals("write")) {
                    writeQuorum = Integer.parseInt(line[1]);
                    flag++;
                } else if (line[0].equals("files")) {
                    numFiles = Integer.parseInt(line[1]);
                    flag++;
                }
            }

            if (flag != NUM_CONFIG_FIELDS) {
                Log.error(FID, "Only " + flag + " config fields were read. " + NUM_CONFIG_FIELDS + " are required.");
            } else if (numServers < 1) {
                Log.error(FID, "Must set at least one server.");
            } else if (numServers < writeQuorum) {
                Log.error(FID, "Write quorum must be less than or equal than the number of servers.");
            } else if (numServers < readQuorum) {
                Log.error(FID, "Read quorum must be less than or equal than the number of servers.");
            }

            scanConfig.close();
        } catch (Exception exception) {
            Log.error(FID, "Error reading file", exception);
        }
    }


    /**
     * Check that config file was sane.
     */
    void checkConfig() {
        final String FID = "Config.checkConfig()";
        if ((float) writeQuorum <= ((float) numServers / 2.0)) {
            Log.error(FID, "Write quourum size must be greater than half of the number of servers.");
        } else if (writeQuorum + readQuorum <= numServers) {
            Log.error(FID, "Sum of write quourum size and read quorum size must be greater than the number of servers.");
        } else if (numFiles < 1) {
            Log.error(FID, "There must be at least one file in the system.");
        }
    }


    /**
     * Read in machine.txt file.
     */
    void readMachines() {
        final String FID = "Config.readMachines()";
        int index = 0;

        try {
            FileInputStream file = new FileInputStream(MACHINE_FILE_PATH);
            Scanner scanConfig = new Scanner(file);
            String[] line;

            while (scanConfig.hasNextLine()) {
                line = scanConfig.nextLine().split(" ");

                if (line.length != NUM_MACHINE_LINE_ARGS) {
                    Log.error(FID, "Incorrect number of args in machine.txt entry. There were " + line.length + " and there should've been " + NUM_CONFIG_FIELDS + " args per entry.");
                }

                String ip = line[1];
                int port = Integer.parseInt(line[2]);
                int id = Integer.parseInt(line[3]);

                if (line[0].equals("server")) {
                    if (index == numServers) {
                        Log.error(FID, "Too many servers in machines.txt, there should be " + numServers);
                    }
                    servers[index] = new ServerInfo(id, ip, port, false);
                    index++;
                } else if (line[0].equals("coordinator")) {
                    if (index == numServers) {
                        Log.error(FID, "Too many servers in machines.txt, there should be " + numServers);
                    }
                    servers[index] = new ServerInfo(id, ip, port, true);
                    coordinator = servers[index];
                    index++;
                }
            }

            if (index != numServers) {
                Log.error(FID, "Only " + index + " servers were read in. " + numServers + " are required.");
            }

            scanConfig.close();
        } catch (Exception exception) {
            Log.error(FID, "Error reading file", exception);
        }
    }
}
