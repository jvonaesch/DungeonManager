package dungeonmanager.stat;

import java.util.*;

/**
 * Singleton class representing the default set of stat for a creature.
 */
public class BaseStatSet implements WriteableStatSet {

    protected Map<String, Integer> values;

    public BaseStatSet() {
        this.values = new TreeMap<>();
    }

    @Override
    public Integer getValue(String statId) {
        return values.get(statId);
    }

    @Override
    public Set<String> getSpecifiedStats() {
        return values.keySet();
    }

    @Override
    public boolean hasStat(String statId) {
        return values.containsKey(statId);
    }

    @Override
    public void setBaseValue(String statId, Integer value) {
        values.put(statId, value);
    }

    @Override
    public void resetBaseValue(Stat stat) {
        values.put(stat.getId(), stat.getDefaultValue());
    }

    @Override
    public void removeBaseValue(String statId) {
        values.remove(statId);
    }
}
