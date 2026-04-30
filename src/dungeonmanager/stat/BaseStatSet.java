package dungeonmanager.stat;

import java.util.*;

/**
 * Singleton class representing the default set of stat for a creature.
 */
public class BaseStatSet implements WriteableStatSet {

    private final StatContext statContext;
    protected Map<String, Integer> values;

    public BaseStatSet(StatContext statContext) {
        this.values = new TreeMap<>();
        this.statContext = statContext;
    }

    @Override
    public int getValue(String statId) {
        Integer value = values.get(statId);
        Stat stat = statContext.getStat(statId);
        if (value == null) return stat == null ? 0 : stat.getDefaultValue();
        return value;
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
