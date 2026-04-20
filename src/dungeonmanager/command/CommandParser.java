package dungeonmanager.command;

import dungeonmanager.DungeonManagerApp;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    public static final String commandRegex =
        "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|(\'([^\'\\\\]*(\\\\.[^\'\\\\]*)*)\')|\\S+";
    public static final Pattern commandPattern = Pattern.compile(commandRegex);

    public static Matcher getCommandMatcher(String command) {
        return commandPattern.matcher(command);
    }

    public static String stripQuotes(String string) {
        return string.replaceAll("^\"|\"$|^'|'$", "");
    }

    public static String unescapeString(String string) {
        string = string.replaceAll("\\\\n", "\n");
        string = string.replaceAll("\\\\t", "\t");
        return string.replaceAll("\\\\(.)", "$1");
    }

    public static List<String> parseCommand (String command) {
        Matcher matcher = CommandParser.getCommandMatcher(command);
        List<String> tokens = new LinkedList<String> ();

        while (matcher.find()) {
            String token = matcher.group();
            token = stripQuotes(token);
            token = unescapeString(token);
            tokens.add(token);
        }

        return tokens;
    }

    public static final CommandRecord readCommand (DungeonManagerApp context, String command_in) {
        List<String> tokens = parseCommand(command_in);
        String command = tokens.remove(0);
        // String command = tokens.removeFirst();
        String[] args = tokens.toArray(new String[0]);

        return new CommandRecord(context, command, args);
    }
}
