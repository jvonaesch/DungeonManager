package creature;

import ability.Ability;
import ability.AbilityScore;
import ability.AbilityScoreModifier;

import java.util.EnumMap;
import java.util.HashMap;

public abstract class AbstractCreature {

    private final EnumMap<Ability, AbilityScore> abilities;
    private final HashMap<String, Integer> proficiencies;
    String name;

    public AbstractCreature() {
        abilities = Ability.getNewDefaultScores();
        proficiencies = new HashMap<String, Integer> ();
    }

    public void setBaseAbilityScore(Ability ability, int value) {
        abilities.get(ability).setBaseValue(value);
    }

    public void reloadScoreValues() {
        Ability.forEach(ability -> {
            abilities.get(ability).reloadScoreValue();
        });
    }
    public void addScoreModifier(AbilityScoreModifier mod_in) {
        Ability.forEach(ability -> {
            abilities.get(ability).addScoreModifier(mod_in);
        });
    }
    public void removeScoreModifier(AbilityScoreModifier mod_in) {
        Ability.forEach(ability -> {
            this.abilities.get(ability).removeScoreModifier(mod_in);
        });
    }

    public String toString() {
        String str = "Creature: " + name + "\n";
        for (Ability ab: Ability.values()) {
            str += "- " + abilities.get(ab) + "\n";
        }
        return str;
    }
}
