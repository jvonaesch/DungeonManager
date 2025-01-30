package ability;

import java.util.Set;

public interface ModifiableAbilitySet extends AbilitySet {
    public void setBaseScore(Ability ability, Integer value);
    public void removeBaseScore(Ability ability);
    public int getBaseScore(Ability ability);

    public void addModifier(AbilityModifier modifier);
    public boolean removeModifier(AbilityModifier modifier);
    public int getModifierTotal(Ability ability);
    public void reloadScores();
}
