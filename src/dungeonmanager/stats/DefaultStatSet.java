package dungeonmanager.stats;

import java.util.Set;
import java.util.TreeSet;

/**
 * Default implementation of StatSet that provides standard stat values.
 * Returns default values for all stats and tracks which stats are specified.
 * Used as a fallback when no other stat set is available.
 * 
 * @see dungeonmanager.stats.StatSet for the interface
 * @see dungeonmanager.stats.StandardStat for predefined stats
 */
public class DefaultStatSet implements StatSet {

    protected Set<Stat> specified;
    private static DefaultStatSet defaultInstance;

    /**
     * Creates a DefaultStatSet with specified stats.
     * @param specified the stats to include in this set
     */
    public DefaultStatSet(Set<Stat> specified) {
        this.specified = new TreeSet<> (Stats.getDefaultComparator());
        this.specified.addAll(specified);
    }

    /**
     * Gets the singleton default instance containing all standard stats.
     * @return the default stat set instance
     * @see dungeonmanager.stats.StandardStat#values() for included stats
     */
    public static DefaultStatSet get() {
        if (defaultInstance == null) {
            defaultInstance = new DefaultStatSet(Set.of(StandardStat.values()));
        }
        return defaultInstance;
    }

    @Override
    public int getValue(Stat stat) {
        return stat.getDefaultValue();
    }

    @Override
    public Set<Stat> getSpecifiedStats() {
        return specified;
    }
}
