package creature;

import ability.Ability;
import ability.AbilityScore;
import ability.AbilityScoreSet;
import ability.AbilityScoreSetModifier;
import ability.proficiency.ProficiencySet;

import java.util.Set;

public class Creature {

    private AbilityScoreSet abilityScores;
    private ProficiencySet proficiencies;
    private final CreatureType creatureType;
    String name;

    public Creature(Set<Ability> abilities, CreatureType creatureType, String name) {
        this.abilityScores = new AbilityScoreSet(abilities);
        this.creatureType = creatureType;
        this.proficiencies = new ProficiencySet();
        this.name = name;
    }

    public void setBaseAbilityScore(Ability ability, int value) {
        abilityScores.get(ability).setBaseValue(value);
    }

    public void reloadScoreValues() {
        AbilityOld.forEach(ability -> {
            abilityScores.get(ability).reloadScoreValue();
        });
    }
    public void addScoreModifier(AbilityScoreSetModifier mod_in) {
        AbilityOld.forEach(ability -> {
            abilityScores.get(ability).addScoreModifier(mod_in);
        });
    }
    public void removeScoreModifier(AbilityScoreSetModifier mod_in) {
        AbilityOld.forEach(ability -> {
            this.abilityScores.get(ability).removeScoreModifier(mod_in);
        });
    }

    public String toString() {
        String str = "Creature: " + name + "\n";
        for (AbilityOld ab: AbilityOld.values()) {
            str += "- " + abilityScores.get(ab) + "\n";
        }
        return str;
    }
}
