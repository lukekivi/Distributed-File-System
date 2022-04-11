package data;

public class Command {
    private CommandType commandType;
    private int fileId;

    public Command(CommandType commandType, int fileId) {
        this.commandType = commandType;
        this.fileId = fileId;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public int getFileId() {
        return fileId;
    }
}
