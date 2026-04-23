package dungeonmanager.stat;

import java.util.Set;

/**
 * Interface for read-only access to a set of defined stat and their values.
 */
public interface StatSet {

    Integer getValue(String statId);

    default int getValue(String statId, int defaultValue) {
        Integer value = getValue(statId);
        return value != null ? value : defaultValue;
    }

    default int getValue(IStat stat) {
        return getValue(stat.getId(), stat.getDefaultValue());
    }

    Set<String> getSpecifiedStats();
}
