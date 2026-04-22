package dungeonmanager.stat;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a collection of stat modifications that can be applied to a stat set.
 *
 * @see dungeonmanager.stat.ModifiableStatSet#addModifier(dungeonmanager.stat.StatModifier) for application
 * @see dungeonmanager.stat.ModifiableStatSet#removeModifier(dungeonmanager.stat.StatModifier) for removal
 */
public class StatModifier {

    private HashMap<String, Integer> values;

    public StatModifier(Map<String, Integer> values_in) {
        values = new HashMap <> (values_in);
    }

    public StatModifier() {
        this(new HashMap <> ());
    }

    public int getValue (String statId) {
        return values.get(statId);
    }

    public int getValue(@NotNull Stat stat) {
        return values.get(stat.getID());
    }

    public StatModifier setValue (String statId, int value) {
        if (statId == null) {
            throw new IllegalArgumentException("Stat cannot be null");
        }
        values.put(statId, value);
        return this;
    }

    public StatModifier setValue (Stat stat, int value) {
        return this.setValue(stat.getID(), value);
    }

    public HashMap<String, Integer> getValues () {
        return new HashMap <> (values);
    }

    public Set<String> getStats() {
        return values.keySet();
    }

    @Override
    public String toString() {
        return "Modifier %s".formatted(values);
    }
}
