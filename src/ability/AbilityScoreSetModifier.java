package ability;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AbilityScoreSetModifier
{
    private HashMap<Ability, Integer> values;
    private Set<AbilityScoreSet> modified;

    public AbilityScoreSetModifier(Map<Ability, Integer> values_in) {
        values = new HashMap <Ability, Integer> (values_in);
    }

    public AbilityScoreSetModifier() {
        values = new HashMap <Ability, Integer> ();
    }

    public AbilityScoreSetModifier add(Ability ability, Integer value) {
        values.put(ability, value);
        reloadModified();
        return this;
    }

    public int getValue (Ability ability) {
        return values.get(ability);
    }

    public void setValue (Ability ability, int value) {
        if (value == 0) {
            values.remove(ability);
        } else {
            values.put(ability, value);
        }
        reloadModified();
    }

    public HashMap<Ability, Integer> getValues () {
        return new HashMap <Ability, Integer> (values);
    }

    public Set<Ability> getAbilities () {
        return values.keySet();
    }

    public AbilityScoreSetModifier registerSet(AbilityScoreSet set) {
        modified.add(set);
        set.addScoreModifier(this);
        set.reloadScoreValues();
        return this;
    }

    public boolean unregisterSet(AbilityScoreSet set) {
        set.removeScoreModifier(this);
        set.reloadScoreValues();
        return modified.remove(set);
    }

    public void reloadModified () {
        for (AbilityScoreSet set: modified) {
            set.reloadScoreValues();
        }
    }
}
