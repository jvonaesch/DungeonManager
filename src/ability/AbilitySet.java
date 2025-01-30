package ability;

import java.util.Set;

public interface AbilitySet {
    public int getScore(Ability ability);
    public int getDefaultScore();

    public Set<Ability> getSpecified();
}
