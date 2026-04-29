package dungeonmanager.stat;

import dungeonmanager.registry.StatRegistry;

import java.util.Map;
import java.util.Set;


/**
 * Immutable class representing all registered stats and their default values.
 */
public class SessionStatContext {

    private final StatRegistry statRegistry;

    public SessionStatContext(StatRegistry statRegistry) {
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
}
