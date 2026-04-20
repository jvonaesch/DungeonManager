package dungeonmanager.stats;

import dungeonmanager.registry.Registries;

import java.util.Set;

/**
 * Interface for read-only access to a set of defined stats and their values.
 */
public interface StatSet {

    public int getValue(Stat stat);
    public Set<Stat> getSpecifiedStats();

    default public int getValue(String stat_id) {
        Stat stat = Registries.get().stats.get(stat_id);
        if (stat == null) {
            throw new IllegalArgumentException("Stat not in registry: " + stat_id);
        }
        return getValue(stat);
    }
}
