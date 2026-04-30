package dungeonmanager.stat;

import dungeonmanager.registry.StatRegistry;

import java.util.Map;
import java.util.Set;


/**
 * Immutable class representing all registered stats and their default values.
 */
public class StatContext {

    private final StatRegistry statRegistry;

    public StatContext(StatRegistry statRegistry) {
        this.statRegistry = statRegistry;
    }

    public Map<String, Integer> getDefaults() {
        return statRegistry.getDefaultValues();
    }

    public Stat getStat(String statId) {
        return statRegistry.get(statId);
    }

    public Set<String> getAllIDs() {
        return statRegistry.getAllKeys();
    }

    public Map<String, Stat> getAllStats() {
        return statRegistry.getAll();
    }

    public int getStatDefault(String statId) {
        return statRegistry.getDefaultValues().getOrDefault(statId, 0);
    }
}
