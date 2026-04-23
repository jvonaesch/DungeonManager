package dungeonmanager.stat;

import java.util.Comparator;

/**
 * Utility class for stat-related operations and constants.
 * Provides stat comparison utilities and string formatting helpers.
 * 
 * @see dungeonmanager.stat.StatSets for advanced stat set formatting
 * @see dungeonmanager.stat.ModifiableStatSet for stat set implementations
 */
public class Stats {

    public static String toString(IStat ability) {
        return ability.getId();
    }

    /**
     * Enumeration providing stat comparison functionality.
     * Used for consistent ordering of stat in collections (TreeSet, TreeMap).
     */
    public enum AbilityComparator implements Comparator<IStat> {
        DEFAULT;

        @Override
        public int compare(IStat o1, IStat o2) {
            return o1.getId().compareTo(o2.getId());
        }
    }

    /**
     * Gets the default comparator for stat (compares by ID).
     * @return comparator that orders stat alphabetically by ID
     * @see dungeonmanager.stat.Stats.AbilityComparator for implementation
     */
    public static Comparator<? super IStat> getDefaultComparator () {
        return AbilityComparator.DEFAULT;
    }
}
