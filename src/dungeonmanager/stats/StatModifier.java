package dungeonmanager.stats;

import dungeonmanager.registry.Registries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatModifier {

    private HashMap<Stat, Integer> values;

    public StatModifier(Map<Stat, Integer> values_in) {
        values = new HashMap <Stat, Integer> (values_in);
    }

    public StatModifier() {
        this(new HashMap <Stat, Integer> ());
    }

    public StatModifier add(Stat ability, Integer value) {
        values.put(ability, value);
        return this;
    }

    public int getValue (Stat ability) {
        return values.get(ability);
    }

    public int getValue (String ability_id) {
        Stat ability = Registries.get().stats.get(ability_id);
        return values.get(ability);
    }

    public StatModifier setValue (Stat ability, int value) {
        if (value == 0) values.remove(ability);
        else values.put(ability, value);
        return this;
    }

    public StatModifier setValue (String ability_id, int value) {
        Stat ability = Registries.get().stats.get(ability_id);
        return setValue(ability, value);
    }

    public HashMap<Stat, Integer> getValues () {
        return new HashMap <Stat, Integer> (values);
    }

    public Set<Stat> getAbilities () {
        return values.keySet();
    }

    @Override
    public String toString() {
        return "Modifier %s".formatted(values);
    }
}
