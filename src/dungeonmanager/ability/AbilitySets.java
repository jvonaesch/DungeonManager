package dungeonmanager.ability;

public class AbilitySets {
    public static String toString(AbilitySet set) {
        String string = "Ability set: ";
        for (Ability ability: set.getSpecified()) {
            string += "\n > "+ability.getID()+": "+set.getScore(ability)+"";
        }
        return string;
    }
}
