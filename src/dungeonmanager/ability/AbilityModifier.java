package dungeonmanager.ability;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AbilityModifier {

    private HashMap<Ability, Integer> values;

    public AbilityModifier(Map<Ability, Integer> values_in) {
        values = new HashMap <Ability, Integer> (values_in);
    }

    public AbilityModifier() {
        this(new HashMap <Ability, Integer> ());
    }

    public AbilityModifier add(Ability ability, Integer value) {
        values.put(ability, value);
        return this;
    }

    public int getValue (Ability ability) {
        return values.get(ability);
    }

    public void setValue (Ability ability, int value) {
        if (value == 0) values.remove(ability);
        else values.put(ability, value);
    }

    public HashMap<Ability, Integer> getValues () {
        return new HashMap <Ability, Integer> (values);
    }

    public Set<Ability> getAbilities () {
        return values.keySet();
    }
}
