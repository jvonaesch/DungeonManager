package dungeonmanager.stat;

import dungeonmanager.contentpack.JsonLoadable;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Interface for read-only access to a set of defined stat and their values.
 */
public interface StatSet extends JsonLoadable<StatSet> {

    Integer getValue(String statId);

    default int getValue(String statId, int defaultValue) {
        Integer value = getValue(statId);
        return value != null ? value : defaultValue;
    }

    default int getValue(IStat stat) {
        return getValue(stat.getId(), stat.getDefaultValue());
    }

    default Map<String, Integer> getValues() {
        Map<String, Integer> values = new TreeMap<>();
        for (String statId : getSpecifiedStats()) values.put(statId, this.getValue(statId));
        return values;
    }

    Set<String> getSpecifiedStats();
}
