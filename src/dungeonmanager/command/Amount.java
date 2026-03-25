package dungeonmanager.command;

public class Amount {
    public static int parseAmount(String str_in) {
        switch (str_in) {
            case "a": return 1;
            case "one": return 1;
            case "two": return 2;
            case "three": return 3;
            default:
                try {
                    return Integer.parseInt(str_in);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(e.getMessage());
                }
        }
    }
}
