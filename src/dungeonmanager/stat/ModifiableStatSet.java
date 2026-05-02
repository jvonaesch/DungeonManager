package dungeonmanager.stat;

import com.fasterxml.jackson.databind.JsonNode;
import dungeonmanager.session.Session;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

/**
 * Mutable implementation of StatSet that supports base values and modifiers.
 * Manages the core stat calculation logic: base_value + modifier_total = final_score.
 * Stores only base values when serialized
 * Modifiers are organized by target stat and evaluated using depth-first search.
 * Circular dependencies are detected and throw an exception.
 * @see dungeonmanager.stat.StatSet for read-only interface
 * @see dungeonmanager.stat.DefaultedStatSet for inheritance-based delegation
 */
public class ModifiableStatSet implements WriteableStatSet {

    final Map<String, Set<StatModifier>> modifiers;
    final Map<String, Set<String>> dependencyChildren;

    final Map<String, Integer> baseValues;
    final Map<String, Integer> modifierValues;
    final Set<String> dirtyStats;
    protected final StatContext statContext;

    public ModifiableStatSet(StatContext statContext) {
        this.modifiers = new HashMap<>();
        this.dependencyChildren = new HashMap<>();
        this.baseValues = new HashMap<>();
        this.modifierValues = new HashMap<>();
        this.dirtyStats = new HashSet<>();
        this.statContext = statContext;
    }

    @Override
    public void setBaseValue(String statId, Integer value) {
        Stat stat = statContext.getStat(statId);
        if (value == null || (stat != null && value.equals(stat.getDefaultValue()))) {
            this.removeBaseValue(statId);
            return;
        }
        this.baseValues.put(statId, value);
        this.markDirty(statId);
    }

    public int getBaseValue(String statId) {
        Integer value = getBaseValues().get(statId);
        return value == null ? statContext.getStatDefault(statId) : value;
    }

    public Map<String, Integer> getBaseValues() {
        return Collections.unmodifiableMap(baseValues);
    }

    @Override
    public void removeBaseValue(String statId) {
        baseValues.remove(statId);
        this.markDirty(statId);
    }

    @Override
    public void resetBaseValue(Stat stat) {
        baseValues.remove(stat.getId());
        this.markDirty(stat.getId());
    }

    public Integer getModifierValue(String statId) {
        return getValue(statId, new HashSet<> ());
    }

    public int getModifierValue(String statId, Set<String> visiting) {
        if (!dirtyStats.contains(statId) && modifierValues.containsKey(statId))
            return modifierValues.get(statId);
        if (visiting.contains(statId)) {
            throw new IllegalStateException("Circular dependency found involving stat: " + statId);
        }
        
        visiting.add(statId);
        int modifierTotal = 0;

        Set<StatModifier> modifierSet = modifiers.get(statId);
        if (modifierSet != null) for (StatModifier modifier: modifierSet) {
            modifierTotal += modifier.getBaseValue();
            for (String sourceId : modifier.getDependencies().keySet()) {
                int sourceValue = getValue(sourceId, visiting);
                float factor = modifier.getDependencyFactor(sourceId);
                modifierTotal += (int) Math.floor(sourceValue * factor);
            }
        }

        visiting.remove(statId);
        modifierValues.put(statId, modifierTotal);
        dirtyStats.remove(statId);
        return modifierTotal;
    }

    @Override
    public int getValue(String statId) {
        return getValue(statId, new HashSet<> ());
    }

    public int getValue(String statId, Set<String> visiting) {
        return getBaseValue(statId) + getModifierValue(statId, visiting);
    }

    @Override
    public Set<String> getSpecifiedStats() {
        Set<String> specified = new HashSet<>(getBaseValues().keySet());
        specified.addAll(modifiers.keySet());
        return specified;
    }

    public void addModifier(@NotNull StatModifier modifier) {
        String targetStatId = modifier.getTargetStatId();

        modifiers.computeIfAbsent(targetStatId, k -> new HashSet<>()).add(modifier);

        for (String sourceId : modifier.getDependencies().keySet()) {
            dependencyChildren.computeIfAbsent(sourceId, k -> new HashSet<>()).add(targetStatId);
        }

        markDirty(targetStatId);
        getValue(targetStatId); // trigger calculation to detect circular dependencies immediately
    }

    public boolean removeModifier(@NotNull StatModifier modifier) {
        String targetStatId = modifier.getTargetStatId();

        Set<StatModifier> targetModifiers = modifiers.get(targetStatId);
        if (targetModifiers == null || !targetModifiers.remove(modifier)) {
            return false;
        }
        if (targetModifiers.isEmpty()) modifiers.remove(targetStatId);

        Set<String> children = dependencyChildren.computeIfAbsent(targetStatId, k -> new HashSet<> ());
        children.clear();

        for (StatModifier targetModifier : targetModifiers) {
            children.addAll(targetModifier.getDependencies().keySet());
        }

        markDirty(targetStatId);
        return true;
    }

    void markDirty(String statId) {
        if (dirtyStats.contains(statId)) return;
        dirtyStats.add(statId);
        modifierValues.remove(statId);

        Set<String> children = dependencyChildren.get(statId);
        if (children != null) {
            for (String childId : new HashSet<>(children)) {
                markDirty(childId);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("Ability set: ");
        for (String statId : getSpecifiedStats()) {
            string.append("\n > ").append(statId).append(": ").append(getValue(statId));
        }
        return string.toString();
    }

    public static ModifiableStatSet fromJson(String id, JsonNode json, Session session) {
        if (!json.isObject()) throw new RuntimeException("ModifiableStatSet json must be an ObjectNode");
        ModifiableStatSet statSet = new ModifiableStatSet(session.getStatContext());
        json.fieldNames().forEachRemaining((String statId) -> statSet.setBaseValue(statId, json.get(statId).intValue()));
        return statSet;
    }
}
