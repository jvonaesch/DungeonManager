package dungeonmanager.stat;

import dungeonmanager.contentpack.JsonLoadable;
import dungeonmanager.contentpack.JsonSerializable;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Interface for read-only access to a set of defined stat and their values.
 */
public interface StatSet extends JsonSerializable {

    /**
     * Get the value of a stat by its ID. Returns null if the stat is not defined in this set.
     * @param statId the ID of the stat to retrieve
     * @return the value of the stat, or null if not defined
     */
    int getValue(String statId);

    default int getValue(String statId, int defaultValue) {
        if (!hasStat(statId)) return 0;
        Integer value = getValue(statId);
        return value != null ? value : defaultValue;
    }

    default int getValue(Stat stat) {
        return getValue(stat.getId(), stat.getDefaultValue());
    }

    default Map<String, Integer> getValues() {
        Map<String, Integer> values = new TreeMap<>();
        for (String statId : getSpecifiedStats()) values.put(statId, this.getValue(statId));
        return values;
    }

    Set<String> getSpecifiedStats();

    default boolean hasStat(String statId) {
        return getSpecifiedStats().contains(statId);
    };
}
