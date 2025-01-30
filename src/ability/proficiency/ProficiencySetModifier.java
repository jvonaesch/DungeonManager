package ability.proficiency;

import ability.Ability;

import java.util.ArrayList;
import java.util.HashMap;

public class ProficiencySetModifier {

    private final HashMap<String, Integer> values;
    private int defaultBonus;

    public ProficiencySetModifier() {
        values = new HashMap <String, Integer> ();
        defaultBonus = 2;
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

    public int getValue (Ability ability) {return values.get(ability); }
}
