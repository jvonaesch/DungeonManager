package dungeonmanager.die;


import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record DieSet(int die, int amt) {
    static Random random = new Random();

    public Integer [] roll() {
        return random.ints(this.amt, 1, 1 + this.die).boxed().toArray(Integer[]::new);
    }

    public static DieSet parseDie(String str) {
        str = str.toLowerCase().replaceAll("^d|s$", "");
        try {
            return new DieSet(Integer.parseInt(str), 1);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Single die format not valid: \"%s\"".formatted(str));
        }
    }

    public static DieSet parseDice(String str, Matcher matcher) {
        int amt = 1;
        int die;

        if (matcher.find()) {
            String match = matcher.group();
            if (match.equals("d")) {
                if (!matcher.find()) throw new NumberFormatException("die not specified");
                try {die = Integer.parseInt(matcher.group()); }
                catch (NumberFormatException e) {throw e; }
            } else {
                if (!matcher.find()) {
                    try {die = Integer.parseInt(match); }
                    catch (NumberFormatException e) {throw e; }
                } else {
                    if (!matcher.group().equals("d")) {
                        throw new NumberFormatException("Invalid die format");
                    }
                    if (!matcher.find()) throw new NumberFormatException("die not specified");
                    try {
                        amt = Integer.parseInt(match);
                        die = Integer.parseInt(matcher.group());
                    } catch (NumberFormatException e) {throw e; }
                }
            }
        } else throw new NumberFormatException("empty dice string");

        return new DieSet(die, amt);
    }

    public static DieSet parseDice(String str) {
        String regex = "[0-9]+|d|\\S+";
        Pattern pattern = Pattern.compile(regex);
        str = str.toLowerCase().replaceAll("s$", "");
        Matcher matcher = pattern.matcher(str);
        return parseDice(str, matcher);
    }

    @Override
    public String toString() {
        return "d%s".formatted(die);
    }

    public DieSet withAmount(int amt) {
        return new DieSet(this.die, amt);
    }
}
