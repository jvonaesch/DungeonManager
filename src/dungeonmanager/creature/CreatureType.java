package dungeonmanager.creature;

import dungeonmanager.stat.HasStatSet;

public interface CreatureType extends HasStatSet {

    String getName ();
    String getID ();
}
