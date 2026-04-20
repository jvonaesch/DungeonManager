package dungeonmanager.creature;

import dungeonmanager.stats.HasStatSet;

public interface CreatureType extends HasStatSet {

    String getName ();
    String getID ();
}
