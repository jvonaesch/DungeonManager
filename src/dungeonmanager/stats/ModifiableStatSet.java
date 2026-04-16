package dungeonmanager.stats;

import java.util.*;

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


    public void setBaseValue(Stat stat, Integer value) {
        if (value == null) this.base_values.remove(stat);
        else this.base_values.put(stat, value);
        this.reloadValues();
    }

    public int getBaseValue(Stat stat) {
        if (base_values.containsKey(stat)) return base_values.get(stat);
        return getDefaultValue();
    }

    @Override
    public int getValue(Stat stat) {
        if (scores.containsKey(stat)) return scores.get(stat);
        else if (base_values.containsKey(stat)) return base_values.get(stat);
        return getDefaultValue();
    }

    public int getModifierTotal(Stat stat) {
        Integer value = modifier_values.get(stat);
        return (value == null) ? 0 : value;
    }

    public void removeBaseValue(Stat stat) {
        base_values.remove(stat);
        this.reloadValues();
    }

    public void resetBaseValue(Stat stat) {
        setBaseValue(stat, getDefaultValue());
    }

    @Override
    public Set<Stat> getSpecifiedStats() {
        return base_values.keySet();
    }

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

    public String toString() {
        StringBuilder string = new StringBuilder("Ability set: ");
        for (Stat stat : getSpecifiedStats()) {
            string.append("\n > ").append(stat.getID()).append(": ").append(getValue(stat));
        }
        return string.toString();
    }
}
