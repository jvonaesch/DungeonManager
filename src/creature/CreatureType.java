package creature;

import ability.AbilityScoreSet;

public interface CreatureType {

    public String getID ();
    public String getName ();

    public AbilityScoreSet getAbilityScores ();
}
