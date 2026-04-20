package dungeonmanager.stats;

import dungeonmanager.registry.Registries;

public interface WriteableStatSet extends StatSet {

    void setBaseValue(Stat stat, Integer value);
    void resetBaseValue(Stat stat);
    void removeBaseValue(Stat stat);

    default void setBaseValue(String stat_ID, Integer value) {
        Stat stat = Registries.get().stats.get(stat_ID);
        if (stat == null) {
            throw new IllegalArgumentException("Stat not in registry: " + stat_ID);
        }
        setBaseValue(stat, value);
    }

    default void resetBaseValue(String stat_ID) {
        Stat stat = Registries.get().stats.get(stat_ID);
        if (stat == null) {
            throw new IllegalArgumentException("Stat not in registry: " + stat_ID);
        }
        resetBaseValue(stat);
    }

    default void removeBaseValue(String stat_ID) {
        Stat stat = Registries.get().stats.get(stat_ID);
        if (stat == null) {
            throw new IllegalArgumentException("Stat not in registry: " + stat_ID);
        }
        removeBaseValue(stat);
    }
}
