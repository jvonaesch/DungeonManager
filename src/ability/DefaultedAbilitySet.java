package ability;

import java.util.Set;
import java.util.TreeSet;

public class DefaultedAbilitySet extends BaseAbilitySet {

    private HasAbilitySet parent;
    private AbilitySet parentSet;
    private Set<Ability> removed;

    public DefaultedAbilitySet(HasAbilitySet parent) {
        this(parent.getAbilitySet());
        this.parent = parent;
    }

    public DefaultedAbilitySet(AbilitySet parentSet) {
        this.parentSet = parentSet;
        this.removed = new TreeSet<Ability> (Abilities.getDefaultComparator());
    }

    @Override
    public void removeBaseScore(Ability ability) {
        super.removeBaseScore(ability);
        base_scores.remove(ability);
        modifier_values.remove(ability);
        scores.remove(ability);
        removed.add(ability);
        reloadScores();
    }

    @Override
    public void resetBaseScore(Ability ability) {
        this.removed.remove(ability);
        super.reloadScores();
    }

    @Override
    public int getBaseScore(Ability ability) {
        if (removed.contains(ability)) return getDefaultScore();
        else if (base_scores.containsKey(ability)) return base_scores.get(ability);
        return (parentSet.getScore(ability));
    }

    @Override
    public int getScore(Ability ability) {
        if (removed.contains(ability)) return getDefaultScore();
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
        a.removeAll(removed);
        return a;
    }

    @Override
    public void reloadScores() {
        if (parent != null && parent.getAbilitySet() != parentSet) parentSet = parent.getAbilitySet();
        super.reloadScores();
    }
}
