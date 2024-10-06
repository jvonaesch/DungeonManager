package ability;

import creature.AbstractCreature;
import util.IDestroyable;

import java.util.ArrayList;
import java.util.EnumMap;

public class AbilityScoreModifier implements IDestroyable {

    private EnumMap<Ability, Integer> values;
    public final ArrayList<AbstractCreature> targets;

    public AbilityScoreModifier(Ability ability, int value) {
        values = new EnumMap <Ability, Integer> (Ability.class);
        targets = new ArrayList<AbstractCreature> ();
        setValue(ability, value);
        init();
    }

    public AbilityScoreModifier() {
        values = new EnumMap <Ability, Integer> (Ability.class);
        targets = new ArrayList<AbstractCreature> ();
        init();
    }

    public AbilityScoreModifier addTo(AbstractCreature target) {
        target.addScoreModifier(this);
        targets.add(target);
        return this;
    }

    public void init() {
        Ability.populateAbilityMap(values);
    }

    public void destroy () {
        for (AbstractCreature target: targets) {
            target.removeScoreModifier(this);
        }
    }

    public void setValue (Ability ability, int value) {
        values.put(ability, value);
        for (AbstractCreature target: targets) {
            target.reloadScoreValues();
        }
    }
    public int getValue (Ability ability) {return values.get(ability); }
}
