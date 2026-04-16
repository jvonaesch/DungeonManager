package dungeonmanager.creature;

import dungeonmanager.stats.HasStatSet;

public interface CreatureType extends HasStatSet {

    public String getName ();
    public String getID ();
}
