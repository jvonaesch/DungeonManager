package dungeonmanager.stats;

import java.util.Comparator;

public class Stats {

    public static String toString(Stat ability) {
        return ability.getID();
    }

    public enum AbilityComparator implements Comparator<Stat> {
        DEFAULT;

        @Override
        public int compare(Stat o1, Stat o2) {
            return o1.getID().compareTo(o2.getID());
        }
    }

    public static Comparator<? super Stat> getDefaultComparator () {
        return AbilityComparator.DEFAULT;
    }
}
