package ability;

import creature.AbstractCreature;

import java.util.EnumMap;

public class AbilityScoreModifier {

    private EnumMap<Ability, Integer> values;
    public final AbstractCreature parent;

    public AbilityScoreModifier(AbstractCreature parent_in, Ability ability, int value) {
        values = new EnumMap <Ability, Integer> (Ability.class);
        parent = parent_in;
        setValue(ability, value);
        init();
    }

    public AbilityScoreModifier(AbstractCreature parent_in) {
        values = new EnumMap <Ability, Integer> (Ability.class);
        parent = parent_in;
        init();
    }

    public void init() {
        Ability.populateAbilityMap(values);
        parent.addScoreModifier(this);
    }

    public void destroy () {
        parent.removeScoreModifier(this);
    }

    public void setValue (Ability ability, int value) {
        values.put(ability, value);
        parent.reloadScoreValues();
    }
    public int getValue (Ability ability) {return values.get(ability); }
}
