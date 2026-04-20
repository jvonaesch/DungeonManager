package dungeonmanager.stats;

import dungeonmanager.registry.Registries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a collection of stat modifications that can be applied to a stat set.
 *
 * @see dungeonmanager.stats.ModifiableStatSet#addModifier(dungeonmanager.stats.StatModifier) for application
 * @see dungeonmanager.stats.ModifiableStatSet#removeModifier(dungeonmanager.stats.StatModifier) for removal
 */
public class StatModifier {

    private HashMap<Stat, Integer> values;

    /**
     * Creates a StatModifier with initial values from a map.
     *
     * @param values_in initial stat modifications
     */
    public StatModifier(Map<Stat, Integer> values_in) {
        values = new HashMap <Stat, Integer> (values_in);
    }

    /**
     * Creates an empty StatModifier.
     */
    public StatModifier() {
        this(new HashMap <Stat, Integer> ());
    }

    /**
     * Gets the modification value for a stat.
     *
     * @param stat the stat to query
     * @return the modification value, or null if not set
     */
    public int getValue (Stat stat) {
        return values.get(stat);
    }

    /**
     * Gets the modification value for a stat by ID string.
     *
     * @param ability_id the stat ID to query
     * @return the modification value, or null if not set
     */
    public int getValue (String ability_id) {
        Stat ability = Registries.get().stats.get(ability_id);
        if (ability == null) {
            return 0;
        }
        Integer value = values.get(ability);
        return value == null ? 0 : value;
    }

    /**
     * Sets the modification value for a stat.
     * Values of 0 are automatically removed to save space.
     *
     * @param ability the stat to modify
     * @param value the modification value
     * @return this StatModifier for method chaining
     */
    public StatModifier setValue (Stat ability, int value) {
        if (ability == null) {
            throw new IllegalArgumentException("Stat cannot be null");
        }
        if (Registries.get().stats.get(ability.getID()) == null) {
            Registries.get().stats.register(ability.getID(), ability);
        }
        values.put(ability, value);
        return this;
    }

    /**
     * Sets the modification value for a stat by ID string.
     *
     * @param ability_id the stat ID to modify
     * @param value the modification value
     * @return this StatModifier for method chaining
     */
    public StatModifier setValue (String ability_id, int value) {
        if (ability_id == null || ability_id.isBlank()) {
            throw new IllegalArgumentException("Stat ID cannot be null or blank");
        }
        Stat ability = Registries.get().stats.get(ability_id);
        if (ability == null) {
            ability = new CustomStat(ability_id, ability_id, "other", 0);
            Registries.get().stats.register(ability_id, ability);
        }
        return setValue(ability, value);
    }

    /**
     * @return new HashMap containing all stat modifications
     */
    public HashMap<Stat, Integer> getValues () {
        return new HashMap <Stat, Integer> (values);
    }

    /**
     * @return set of stats with modifications
     */
    public Set<Stat> getStats() {
        return values.keySet();
    }

    @Override
    public String toString() {
        return "Modifier %s".formatted(values);
    }
}
