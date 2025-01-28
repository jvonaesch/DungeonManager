package ability.proficiency;

import creature.AbstractCreature;

import java.util.ArrayList;
import java.util.HashMap;

public class ProficiencySetModifier {

    private final HashMap<String, Integer> values;
    private final ArrayList<AbstractCreature> targets;
    private int defaultBonus;

    public ProficiencySetModifier() {
        values = new HashMap <String, Integer> ();
        targets = new ArrayList<AbstractCreature> ();
        defaultBonus = 2;
    }

    public ProficiencySetModifier addTo(AbstractCreature target) {
        return this;
    }
    public ProficiencySetModifier setDefaultBonus(int value) {
        this.defaultBonus = value;
        return this;
    }

    public void setProficiency (String prof_id, int value) {
        values.put(prof_id, value);
    }

    public void removeProficiency (String prof_id) {
        values.remove(prof_id);
    }

    public int getValue (AbilityOld ability) {return values.get(ability); }
}
