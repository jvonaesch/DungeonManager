package ability;

import java.util.Comparator;

public class AbilitySets {
    public static String toString(AbilitySet set) {
        String string = "Ability set: ";
        for (Ability ability: set.getSpecified()) {
            string += "\n > "+ability.getShortName()+": "+set.getScore(ability)+"";
        }
        return string;
    }
}
