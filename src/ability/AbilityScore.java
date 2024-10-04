package ability;

import java.util.ArrayList;

public class AbilityScore {
    private int value;
    private ArrayList<AbilityScoreModifier> modifiers;
    public final Ability ability;

    public AbilityScore (Ability ability_in) {
        ability = ability_in;
        value = 10;
        modifiers = new ArrayList<AbilityScoreModifier>();
    }

    public void addScoreModifier(AbilityScoreModifier mod_in) {
        if (!modifiers.contains(mod_in)) {
            modifiers.add(mod_in);
            this.reloadScoreValue();
        }
    }

    public boolean removeScoreModifier(AbilityScoreModifier mod_in) {
        boolean found = modifiers.remove(mod_in);
        this.reloadScoreValue();
        return found;
    }

    public void reloadScoreValue() {
        value = 10;
        for (AbilityScoreModifier modifier : modifiers) {
            value += modifier.getValue(ability);
        }
    }

    public int getValue() {return value; }
    public void setValue(int val_in) {value = val_in; }
    public int getModifier() {return (value - 10) / 2; }

    public String toString() {
        return ability.name + " Score: " + this.getValue() + " , Modifier: " + this.getModifier();
    }

}
