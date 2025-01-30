package ability;

import java.util.HashSet;
import java.util.Set;

public class DefaultedAbilitySet extends BaseAbilitySet {

    private HasAbilities parent;
    private AbilitySet parent_set;

    public DefaultedAbilitySet(HasAbilities parent) {
        super();
        this.parent = parent;
        this.parent_set = parent.getAbilitySet();
    }

    @Override
    public int getBaseScore(Ability ability) {
        if (base_scores.containsKey(ability)) return base_scores.get(ability);
        else return (parent_set.getScore(ability));
    }

    @Override
    public int getScore(Ability ability) {
        if (base_scores.containsKey(ability)) return scores.get(ability);
        else return (parent_set.getScore(ability) + this.getModifierTotal(ability));
    }

    @Override
    public int getDefaultScore() {
        return parent_set.getDefaultScore();
    }

    @Override
    public Set<Ability> getSpecified() {
        Set<Ability> a = new HashSet<Ability>(parent_set.getSpecified());
        a.addAll(super.getSpecified());
        return a;
    }
}
