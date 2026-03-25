package dungeonmanager.command;

import dungeonmanager.DungeonManagerApp;

public class CommandRecord <T> {
    private final String command;
    private final String[] args;
    private T return_value;
    private String message;
    private DungeonManagerApp context;

    public CommandRecord(DungeonManagerApp context, String command, String[] args) {
        this.command = command;
        this.args = args;
        this.return_value = null;
        this.message = "Empty command record";
        this.context = context;
    }

    public String getCommand() {return command; }
    public String[] getArgs() {return args; }

    public void setReturn(T value, String message) {
        this.return_value = value;
        this.message = message;
    }

    public String getMessage() {return message; }
    public T getReturnValue() {return return_value; }
    public DungeonManagerApp getContext() {return context; }
}
