package dungeonmanager.creature;

import dungeonmanager.ability.HasAbilitySet;

public interface CreatureType extends HasAbilitySet {

    public String getName ();
    public String getID ();
}
