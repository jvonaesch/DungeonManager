package dungeonmanager.stat;

import java.util.*;

/**
 * Mutable implementation of StatSet that supports base values and modifiers.
 * Manages the core stat calculation logic: base_value + modifier_total = final_score.
 * Stores only base values when serialized
 * 
 * Modifiers are organized by target stat and evaluated using depth-first search.
 * Circular dependencies are detected and throw an exception.
 * 
 * @see dungeonmanager.stat.StatSet for read-only interface
 * @see dungeonmanager.stat.DefaultedStatSet for inheritance-based delegation
 */
public class ModifiableStatSet implements WriteableStatSet {

    // Map of targetStat -> Set of modifiers that modify that stat
    final Map<String, Set<StatModifier>> modifiers;
    final Map<String, Integer> baseValues;
    Map<String, Integer> values;

    public ModifiableStatSet() {
        this.modifiers = new HashMap<>();
        this.baseValues = new HashMap<>();
        this.values = new HashMap<>();
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

    /**
     * Compute the total modifier value for a given stat, assuming all dependencies are already computed.
     */
    public int getModifierTotal(String statId) {
        if (!modifiers.containsKey(statId)) return 0;
        int total = 0;
        Set<StatModifier> statsModifiers = modifiers.get(statId);

        for (StatModifier modifier : statsModifiers) {
            total += modifier.getBaseValue();
            for (Map.Entry<String, Float> dep : modifier.getDependencies().entrySet()) {
                String dependentStat = dep.getKey();
                float factor = dep.getValue();
                Integer summand = getValue(dependentStat);
                if (summand != null) {
                    total += (int) Math.floorDiv((long)(summand * factor), 1L);
                }
            }
        }
        return total;
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
        Set<String> specified = new HashSet<>(baseValues.keySet());
        specified.addAll(modifiers.keySet());
        return specified;
    }

    public void reloadValues() {
        values.clear();
        Set<String> allStats = getSpecifiedStats();

        for (String statId : allStats) {
            if (!values.containsKey(statId)) {
                computeStatValue(statId, new HashSet <> ());
            }
        }
    }

    /**
     * DFS stat computation, no cycles
     *
     * @param statId  the ID of the stat to compute
     * @param visiting the set of stats currently being visited (for cycle detection)
     * @throws IllegalStateException if a circular dependency is detected
     */
    private void computeStatValue(String statId, Set<String> visiting) {
        if (visiting.contains(statId)) throw new IllegalStateException(
                "Circular dependency detected involving stat: " + statId);
        if (values.containsKey(statId)) return;
        
        visiting.add(statId);
        
        int baseValue = baseValues.getOrDefault(statId, 0);
        int modifierTotal = 0;
        
        Set<StatModifier> statsModifiers = modifiers.getOrDefault(statId, Set.of());
        for (StatModifier modifier : statsModifiers) {
            modifierTotal += modifier.getBaseValue();
            
            for (Map.Entry<String, Float> dependency : modifier.getDependencies().entrySet()) {
                String dependentStat = dependency.getKey();
                float factor = dependency.getValue();
                
                if (!values.containsKey(dependentStat)) {
                    computeStatValue(dependentStat, visiting);
                }
                
                Integer depValue = values.get(dependentStat);
                if (depValue != null) {
                    modifierTotal += (int)Math.floor(depValue * factor);
                }
            }
        }
        
        visiting.remove(statId);
        values.put(statId, baseValue + modifierTotal);
    }

    public void addModifier(StatModifier modifier) {
        String targetStatId = modifier.getTargetStatId();
        if (!modifiers.containsKey(targetStatId)) {
            Set<StatModifier> targetModifiers = new HashSet<>();
            targetModifiers.add(modifier);
            modifiers.put(targetStatId, targetModifiers);
        }
        modifiers.get(targetStatId).add(modifier);
        this.reloadValues();
    }

    public boolean removeModifier(StatModifier modifier) {
        String targetStatId = modifier.getTargetStatId();
        Set<StatModifier> targetModifiers = modifiers.get(targetStatId);
        if (targetModifiers != null) {
            boolean found = targetModifiers.remove(modifier);
            if (targetModifiers.isEmpty()) {
                modifiers.remove(targetStatId);
            }
            this.reloadValues();
            return found;
        }
        return false;
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
