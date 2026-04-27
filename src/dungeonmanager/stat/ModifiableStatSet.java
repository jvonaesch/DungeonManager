package dungeonmanager.stat;

import java.util.*;

/**
 * Mutable implementation of StatSet that supports base values and modifiers.
 * Manages the core stat calculation logic: base_value + modifier_total = final_score.
 * Stores only base values when serialized
 * 
 * @see dungeonmanager.stat.StatSet for read-only interface
 * @see dungeonmanager.stat.DefaultedStatSet for inheritance-based delegation
 */
public class ModifiableStatSet implements WriteableStatSet {

    public Set<StatModifier> modifiers;
    public Map<String, Integer> baseValues;
    public Map<String, Integer> modifierValues;
    public Map<String, Integer> values;

    public ModifiableStatSet(Map<String, Integer> baseStatValues) {
        this.modifiers = new HashSet<>();
        this.baseValues = new HashMap<>(baseStatValues);
        this.modifierValues = new HashMap<>();
        this.values = new HashMap<>(baseStatValues);
    }

    public ModifiableStatSet() {
        this(new HashMap<>());
    }

    @Override
    public void setBaseValue(String statId, Integer value) {
        if (value == null) this.baseValues.remove(statId);
        else this.baseValues.put(statId, value);
        this.reloadValues();
    }

    public Integer getBaseValue(String statId) {
        if (baseValues.containsKey(statId)) return baseValues.get(statId);
        return null;
    }

    @Deprecated
    public int getBaseValue(String statId, int defaultValue) {
        Integer value = getBaseValue(statId);
        return (value == null) ? defaultValue : value;
    }

    public Map<String, Integer> getBaseValues() {
        return new HashMap<>(baseValues);
    }

    @Override
    public Integer getValue(String stat) {
        if (values.containsKey(stat)) return values.get(stat);
        else if (baseValues.containsKey(stat)) return baseValues.get(stat);
        return null;
    }

    public int getModifierTotal(String statId) {
        if (statId == null || !modifierValues.containsKey(statId)) return 0;
        Integer value = modifierValues.get(statId);
        return (value == null) ? 0 : value;
    }

    @Override
    public void removeBaseValue(String statId) {
        baseValues.remove(statId);
        this.reloadValues();
    }

    @Override
    public void resetBaseValue(Stat stat) {
        setBaseValue(stat.getId(), stat.getDefaultValue());
    }

    @Override
    public Set<String> getSpecifiedStats() {
        Set <String> specified = new HashSet<>(baseValues.keySet());
        specified.addAll(modifierValues.keySet());
        return specified;
    }

    public void reloadValues() {
        modifierValues.clear();
        for (StatModifier modifier: this.modifiers) {
            for (String statId: modifier.getStats()) {
                modifierValues.put(
                        statId,
                        modifier.getValue(statId) + modifierValues.getOrDefault(statId, 0));
            }
        }
        for (String statId: getSpecifiedStats()) {
            values.put(statId, getBaseValue(statId) + getModifierTotal(statId));
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
        for (String statId : getSpecifiedStats()) {
            string.append("\n > ").append(statId).append(": ").append(getValue(statId));
        }
        return string.toString();
    }

    @Override
    public boolean hasStat(String statId) {
        return values.containsKey(statId);
    }
}
