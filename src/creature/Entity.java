package creature;

import ability.*;

import java.util.Set;

public class Entity implements HasAbilities {

    private ModifiableAbilitySet abilities;
    private EntityType type;
    private String name;

    public Entity(EntityType type, String name) {
        this.type = type;
        this.abilities = new DefaultedAbilitySet(type);
        // this.proficiencies = new ProficiencySet();
        this.name = name;
    }

    public void setBaseAbilityScore(Ability ability, int value) {
        abilities.setBaseScore(ability, value);
    }

    public void reloadScoreValues() {
        abilities.reloadScores();
    }

    public void addScoreModifier(AbilityModifier modifier) {
        abilities.addModifier(modifier);
    }

    public void removeScoreModifier(AbilityModifier modifier) {
        abilities.removeModifier(modifier);
    }

    public String toString() {
        return "Creature \"" + name + "\"\n "+abilities;
    }

    @Override
    public AbilitySet getAbilitySet() {
        return abilities;
    }
}
