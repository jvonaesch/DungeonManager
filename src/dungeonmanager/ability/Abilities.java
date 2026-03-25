package dungeonmanager.ability;

import java.util.Comparator;

public class Abilities {

    public static String toString(Ability ability) {
        return ability.getShortName();
    }

    public enum AbilityComparator implements Comparator<Ability> {
        DEFAULT;

        @Override
        public int compare(Ability o1, Ability o2) {
            return o1.getID().compareTo(o2.getID());
        }
    }

    public static Comparator<? super Ability> getDefaultComparator () {
        return AbilityComparator.DEFAULT;
    }
}
