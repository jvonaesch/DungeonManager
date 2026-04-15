package dungeonmanager.command;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandLine {

    private final CommandContext context;
    private Queue<CommandRecord> command_queue;

    public CommandLine(CommandContext context) {
        this.context = context;
        this.command_queue = new ConcurrentLinkedQueue<>();
    }

    public void waitForCommand () {
        String command_in = context.input.nextLine();
        command_queue.add(CommandParser.readCommand(this.context.app, command_in));
    }

    public boolean executeLastCommand() {
        CommandRecord record = command_queue.poll();
        String command_id = record.getCommand();

        try {
            Command command = context.registries.command.get(command_id);
            boolean success = command.execute(record);
            System.out.println(record.getMessage());
            return success;
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }
}
