package ability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum StandardAbilitySet implements AbilitySet {
    DEFAULT(10, Set.of(StandardAbility.values()));

    private Set<Ability> specified;
    private int default_score;

    private StandardAbilitySet(int default_score, Set<Ability> specified) {
        this.specified = specified;
        this.default_score = default_score;
    }

    @Override
    public int getScore(Ability ability) {
        return default_score;
    }

    @Override
    public int getDefaultScore() {
        return default_score;
    }

    @Override
    public Set<Ability> getSpecified() {
        return specified;
    }
}
