package dungeonmanager.command.commands;

import dungeonmanager.command.Command;
import dungeonmanager.command.CommandRecord;

import java.util.Arrays;

public class StopCommand implements Command<Integer> {

    @Override
    public boolean execute(CommandRecord record) {
        String[] args = record.getArgs();

        switch (args.length){
            case 0:
                record.getContext().shutdown();
                break;
            default:
                throw new IllegalArgumentException ("""
                         Invalid argument configuration for command 'stop': %s. \n
                         Does not take any arguments.
                        """.formatted(Arrays.deepToString(args)));
        }

        record.setReturn(null, "Shutting down app");
        return true;
    }
}
