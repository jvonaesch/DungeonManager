package creature;

import ability.HasAbilities;

public interface EntityType extends HasAbilities {

    public String getID ();
    public String getName ();

}
