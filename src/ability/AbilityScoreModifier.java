package ability;

import java.util.EnumMap;

public class AbilityScoreModifier {

    private EnumMap<Ability, Integer> values;

    public AbilityScoreModifier(EnumMap<Ability, Integer> modifiers_in) {
        values = modifiers_in;
        init();
    }
    public AbilityScoreModifier(Ability ability, int value) {
        values = new EnumMap <Ability, Integer> (Ability.class);
        setValue(ability, value);
        init();
    }
    public AbilityScoreModifier() {
        values = new EnumMap <Ability, Integer> (Ability.class);
        init();
    }

    private void init() {
        Ability.populateAbilityMap(values);
    }

    public void setValue (Ability ability, int value) {
        values.put(ability, value);
    }
    public int getValue (Ability ability) {return values.get(ability); }
}
