package dungeonmanager.creature;

import dungeonmanager.stat.HasStatSet;

public interface CreatureType extends HasStatSet {

    public String getName ();
    public String getID ();
}
