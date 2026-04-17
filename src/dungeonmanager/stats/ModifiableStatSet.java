package dungeonmanager.stats;

import java.util.*;

/**
 * Mutable implementation of StatSet that supports base values and modifiers.
 * Manages the core stat calculation logic: base_value + modifier_total = final_score.
 * 
 * @see dungeonmanager.stats.StatSet for read-only interface
 * @see dungeonmanager.stats.DefaultedStatSet for inheritance-based delegation
 */
public class ModifiableStatSet implements StatSet {

    public Set<StatModifier> modifiers;
    public Map<Stat, Integer> base_values;
    public Map<Stat, Integer> modifier_values;
    public Map<Stat, Integer> scores;

    public ModifiableStatSet(Map<Stat, Integer> base_abilities) {
        this.modifiers = new HashSet<>();
        this.base_values = new TreeMap<>(Stats.getDefaultComparator());
        base_values.putAll(base_abilities);
        this.modifier_values = new TreeMap<>(Stats.getDefaultComparator());
        this.scores = new TreeMap<>(Stats.getDefaultComparator());
    }

    public ModifiableStatSet() {
        this(new HashMap<>());
    }

    public ModifiableStatSet(Set<Stat> base_abilities, Integer base_value) {
        this();
        for (Stat ability: base_abilities) this.base_values.put(ability, base_value);
    }

    public ModifiableStatSet(Set<Stat> base_abilities) {
        this(base_abilities, 0);
    }

    /**
     * Sets the base value for a stat and recalculates scores.
     * @param stat the stat to modify
     * @param value the new base value, or null to remove
     * @see dungeonmanager.stats.ModifiableStatSet#reloadValues() for recalculation
     */
    public void setBaseValue(Stat stat, Integer value) {
        if (value == null) this.base_values.remove(stat);
        else this.base_values.put(stat, value);
        this.reloadValues();
    }

    /**
     * Gets the base value for a stat before modifiers.
     * @param stat the stat to query
     * @return the base value, or default if not set
     * @see dungeonmanager.stats.ModifiableStatSet#getDefaultValue() for default logic
     */
    public int getBaseValue(Stat stat) {
        if (base_values.containsKey(stat)) return base_values.get(stat);
        return getDefaultValue();
    }

    /**
     * Gets the final computed value of a stat including modifiers.
     * @param stat the stat to query
     * @return the final score (base + modifiers)
     * @see dungeonmanager.stats.ModifiableStatSet#getModifierTotal(dungeonmanager.stats.Stat) for modifier calculation
     */
    @Override
    public int getValue(Stat stat) {
        if (scores.containsKey(stat)) return scores.get(stat);
        else if (base_values.containsKey(stat)) return base_values.get(stat);
        return getDefaultValue();
    }

    /**
     * Gets the total modifier value for a stat.
     * @param stat the stat to query
     * @return sum of all modifier values for this stat
     */
    public int getModifierTotal(Stat stat) {
        Integer value = modifier_values.get(stat);
        return (value == null) ? 0 : value;
    }

    /**
     * Removes the base value for a stat and recalculates.
     * @param stat the stat to remove
     * @see dungeonmanager.stats.ModifiableStatSet#reloadValues() for recalculation
     */
    public void removeBaseValue(Stat stat) {
        base_values.remove(stat);
        this.reloadValues();
    }

    /**
     * Resets a stat's base value to the default.
     * @param stat the stat to reset
     * @see dungeonmanager.stats.ModifiableStatSet#getDefaultValue() for default value
     */
    public void resetBaseValue(Stat stat) {
        setBaseValue(stat, getDefaultValue());
    }

    /**
     * Gets the set of stats that have been explicitly set.
     * @return stats with base values defined
     */
    @Override
    public Set<Stat> getSpecifiedStats() {
        return base_values.keySet();
    }

    /**
     * Recalculates all modifier totals and final scores.
     * <b>Must</b> be called after any modifier or base value changes.
     */
    public void reloadValues() {
        modifier_values.clear();
        for (StatModifier modifier: this.modifiers) {
            for (Stat ability: modifier.getAbilities()) {
                modifier_values.put(
                        ability,
                        modifier.getValue(ability) + modifier_values.getOrDefault(ability, 0));
            }
        }
        for (Stat ability: getSpecifiedStats()) {
            scores.put(ability, getBaseValue(ability) + getModifierTotal(ability));
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

    public int getDefaultValue() {
        return 0;
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
