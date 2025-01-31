package creature;

import ability.HasAbilitySet;
import registry.RegistryElement;

public interface EntityType extends HasAbilitySet, RegistryElement {

    public String getName ();
}
