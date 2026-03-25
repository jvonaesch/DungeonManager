package dungeonmanager.ability;

import java.util.Set;
import java.util.TreeSet;

public class StandardAbilitySet implements AbilitySet {

    protected Set<Ability> specified;
    protected int defaultScore;
    private static StandardAbilitySet defaultInstance;

    public StandardAbilitySet(int default_score, Set<Ability> specified) {
        this.specified = specified;
        this.defaultScore = default_score;
    }

    public static StandardAbilitySet DEFAULT () {
        if (defaultInstance == null) {
            defaultInstance = new StandardAbilitySet(10, Set.of(StandardAbility.values()));
        }
        return defaultInstance;
    }

    @Override
    public int getScore(Ability ability) {
        return defaultScore;
    }

    @Override
    public int getDefaultScore() {
        return defaultScore;
    }

    @Override
    public Set<Ability> getSpecified() {
        Set<Ability> specified = new TreeSet<Ability> (Abilities.getDefaultComparator());
        specified.addAll(this.specified);
        return specified;
    }
}
