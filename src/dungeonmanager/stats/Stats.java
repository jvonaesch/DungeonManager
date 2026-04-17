package dungeonmanager.stats;

import java.util.Comparator;

/**
 * Utility class for stat-related operations and constants.
 * Provides stat comparison utilities and string formatting helpers.
 * 
 * @see dungeonmanager.stats.StatSets for advanced stat set formatting
 * @see dungeonmanager.stats.ModifiableStatSet for stat set implementations
 */
public class Stats {

    public static String toString(Stat ability) {
        return ability.getID();
    }

    /**
     * Enumeration providing stat comparison functionality.
     * Used for consistent ordering of stats in collections (TreeSet, TreeMap).
     */
    public enum AbilityComparator implements Comparator<Stat> {
        DEFAULT;

        @Override
        public int compare(Stat o1, Stat o2) {
            return o1.getID().compareTo(o2.getID());
        }
    }

    /**
     * Gets the default comparator for stats (compares by ID).
     * @return comparator that orders stats alphabetically by ID
     * @see dungeonmanager.stats.Stats.AbilityComparator for implementation
     */
    public static Comparator<? super Stat> getDefaultComparator () {
        return AbilityComparator.DEFAULT;
    }
}
