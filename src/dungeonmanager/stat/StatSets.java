package dungeonmanager.stat;

public class StatSets {
    public static String toString(StatSet set) {
        String string = "Stat set: ";
        for (Stat ability: set.getSpecified()) {
            string += "\n > "+ability.getID()+": "+set.getScore(ability)+"";
        }
        return string;
    }
}
