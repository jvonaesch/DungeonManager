package dungeonmanager.stat;

import dungeonmanager.library.StatLibrary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Immutable class representing all registered stats and their default values.
 */
public class StatContext {

    private final StatLibrary library;

    public StatContext(StatLibrary statRegistry) {
        this.library = statRegistry;
    }

    public Map<String, Integer> getDefaults() {
        return new HashMap<>(library.getDefaultValues());
    }

    public Stat getStat(String statId) {
        return library.get(statId);
    }

    public Set<String> getAllIDs() {
        return new HashSet<>(library.getAll().keySet());
    }

    @SuppressWarnings("unused")
    public Map<String, Stat> getAllStats() {
        return library.getAll();
    }

    public int getStatDefault(String statId) {
        return getDefaults().getOrDefault(statId, 0);
    }
}
