package creature;

import ability.HasAbilitySet;

public interface EntityType extends HasAbilitySet, RegistryElement {

    public String getName ();
}
