package creature;

import ability.HasAbilitySet;

public interface EntityType extends HasAbilitySet {

    public String getID ();
    public String getName ();

}
