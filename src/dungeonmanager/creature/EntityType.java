package dungeonmanager.creature;

import dungeonmanager.ability.HasAbilitySet;

public interface EntityType extends HasAbilitySet {

    public String getName ();
    public String getID ();
}
