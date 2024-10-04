package ability;

import java.util.EnumMap;

public class AbilityScoreModifier {

    private EnumMap<Ability, Integer> values;
    public final AbilityScore parent;

    public AbilityScoreModifier(AbilityScore parent_in, Ability ability, int value) {
        values = new EnumMap <Ability, Integer> (Ability.class);
        parent = parent_in;
        setValue(ability, value);
        Ability.populateAbilityMap(values);
    }

    public AbilityScoreModifier(AbilityScore parent_in) {
        values = new EnumMap <Ability, Integer> (Ability.class);
        parent = parent_in;
        Ability.populateAbilityMap(values);
    }

    public void setValue (Ability ability, int value) {
        values.put(ability, value);
        parent.reloadScoreValue();
    }
    public int getValue (Ability ability) {return values.get(ability); }
}
