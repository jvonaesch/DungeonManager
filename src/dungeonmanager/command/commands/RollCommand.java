package dungeonmanager.command.commands;

import dungeonmanager.command.Amount;
import dungeonmanager.command.Command;
import dungeonmanager.command.CommandRecord;
import dungeonmanager.die.DieSet;

import java.util.Arrays;

public class RollCommand implements Command<Integer> {

    @Override
    public boolean execute(CommandRecord<Integer> record) {
        int sum = 0;
        DieSet die_set;
        Integer[] rolls;
        String[] args = record.getArgs();

        die_set = switch (args.length) {
            case 1 -> DieSet.parseDice(args[0]);
            case 2 -> DieSet.parseDie(args[1]).withAmount(Amount.parseAmount(args[0]));
            default -> throw new IllegalArgumentException("""
                     Invalid argument configuration for command 'roll': %s. \n
                     Either do 'roll <die>', 'roll <amt> <die>'.
                    """.formatted(Arrays.deepToString(args)));
        };

        rolls = die_set.roll();
        for (int roll: rolls) sum += roll;

        if (die_set.amt() == 1)
            record.setReturn(sum, "Rolled: %s".formatted(sum));
        else
            record.setReturn(sum, "Rolled %s, total: %d".formatted(Arrays.toString(rolls), sum));
        return true;
    }
}
