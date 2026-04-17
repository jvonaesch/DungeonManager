package dungeonmanager.stats;

import java.util.Set;

/**
 * Read-only interface for accessing stat values and metadata.
 * Provides methods to query stat values and discover which stats are explicitly defined.
 * 
 * @see dungeonmanager.stats.ModifiableStatSet for mutable stat set implementation
 * @see dungeonmanager.stats.DefaultedStatSet for inheritance-based delegation
 * @see dungeonmanager.stats.HasStatSet for objects that expose stat sets
 */
public interface StatSet {
    
    /**
     * Gets the current value of a stat, including any modifiers.
     * @param stat the stat to query
     * @return the computed value of the stat
     */
    public int getValue(Stat stat);
    
    /**
     * Gets the set of stats that are explicitly specified in this stat set.
     * @return unmodifiable set of stats that have been defined
     */
    public Set<Stat> getSpecifiedStats();
}
