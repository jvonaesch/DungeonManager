package ability;

import java.util.Set;
import java.util.TreeSet;

public class DefaultedAbilitySet extends BaseAbilitySet {

    private HasAbilitySet parent;
    private AbilitySet parentSet;

    public DefaultedAbilitySet(HasAbilitySet parent) {
        super();
        this.parent = parent;
        this.parentSet = parent.getAbilitySet();
    }

    public DefaultedAbilitySet(AbilitySet parentSet) {
        this.parentSet = parentSet;
    }

    @Override
    public int getBaseScore(Ability ability) {
        if (base_scores.containsKey(ability)) return base_scores.get(ability);
        else return (parentSet.getScore(ability));
    }

    @Override
    public int getScore(Ability ability) {
        if (base_scores.containsKey(ability)) return scores.get(ability);
        else return (parentSet.getScore(ability) + this.getModifierTotal(ability));
    }

    @Override
    public int getDefaultScore() {
        return parentSet.getDefaultScore();
    }

    @Override
    public Set<Ability> getSpecified() {
        Set<Ability> a = new TreeSet<Ability>(Abilities.getDefaultComparator());
        a.addAll(parentSet.getSpecified());
        a.addAll(super.getSpecified());
        return a;
    }

    @Override
    public void reloadScores() {
        if (parent != null && parent.getAbilitySet() != parentSet) parentSet = parent.getAbilitySet();
        super.reloadScores();
    }
}
