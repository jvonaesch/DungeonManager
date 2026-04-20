package dungeonmanager.stats;

import java.util.*;

/**
 * Mutable implementation of StatSet that supports base values and modifiers.
 * Manages the core stat calculation logic: base_value + modifier_total = final_score.
 * 
 * @see dungeonmanager.stats.StatSet for read-only interface
 * @see dungeonmanager.stats.DefaultedStatSet for inheritance-based delegation
 */
public class ModifiableStatSet implements WriteableStatSet {

    public Set<StatModifier> modifiers;
    public Map<Stat, Integer> base_values;
    public Map<Stat, Integer> modifier_values;
    public Map<Stat, Integer> values;

    public ModifiableStatSet(Map<Stat, Integer> base_stats) {
        this.modifiers = new HashSet<>();
        this.base_values = new HashMap<>(base_stats);
        this.modifier_values = new HashMap<>();
        this.values = new HashMap<>(base_stats);
    }

    public ModifiableStatSet() {
        this(new HashMap<>());
    }

    public ModifiableStatSet(Set<Stat> base_stats) {
        this();
        for (Stat stat: base_stats) {
            setBaseValue(stat, stat.getDefaultValue());
        }
    }

    @Override
    public void setBaseValue(Stat stat, Integer value) {
        if (value == null) this.base_values.remove(stat);
        else this.base_values.put(stat, value);
        this.reloadValues();
    }

    public int getBaseValue(Stat stat) {
        if (base_values.containsKey(stat)) return base_values.get(stat);
        return stat.getDefaultValue();
    }

    /**
     * @return copy of assigned base values, excluding inherited defaults.
     */
    public Map<Stat, Integer> getBaseValues() {
        return new HashMap<>(base_values);
    }

    @Override
    public int getValue(Stat stat) {
        if (values.containsKey(stat)) return values.get(stat);
        else if (base_values.containsKey(stat)) return base_values.get(stat);
        return stat.getDefaultValue();
    }

    public int getModifierTotal(Stat stat) {
        Integer value = modifier_values.get(stat);
        return (value == null) ? 0 : value;
    }

    @Override
    public void removeBaseValue(Stat stat) {
        base_values.remove(stat);
        this.reloadValues();
    }

    @Override
    public void resetBaseValue(Stat stat) {
        setBaseValue(stat, stat.getDefaultValue());
    }

    @Override
    public Set<Stat> getSpecifiedStats() {
        Set<Stat> specified = new HashSet<>(base_values.keySet());
        specified.addAll(modifier_values.keySet());
        return specified;
    }

    public void reloadValues() {
        modifier_values.clear();
        for (StatModifier modifier: this.modifiers) {
            for (Stat stat: modifier.getStats()) {
                modifier_values.put(
                        stat,
                        modifier.getValue(stat) + modifier_values.getOrDefault(stat, 0));
            }
        }
        for (Stat stat: getSpecifiedStats()) {
            values.put(stat, getBaseValue(stat) + getModifierTotal(stat));
        }
    }

    public void addModifier(StatModifier modifier) {
        if (!modifiers.contains(modifier)) {
            modifiers.add(modifier);
            this.reloadValues();
        }
    }

    public boolean removeModifier(StatModifier modifier) {
        boolean found = modifiers.remove(modifier);
        this.reloadValues();
        return found;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("Ability set: ");
        for (Stat stat : getSpecifiedStats()) {
            string.append("\n > ").append(stat.getID()).append(": ").append(getValue(stat));
        }
        return string.toString();
    }
}
