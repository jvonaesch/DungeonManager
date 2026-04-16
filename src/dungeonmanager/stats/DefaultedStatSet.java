package dungeonmanager.stats;

import java.util.Set;
import java.util.TreeSet;

public class DefaultedStatSet extends BaseStatSet {

    private HasStatSet parent;
    private StatSet parentSet;
    private final Set<Stat> removed;

    public DefaultedStatSet(HasStatSet parent) {
        this(parent.getStatSet());
        this.parent = parent;
    }

    public DefaultedStatSet(StatSet parentSet) {
        this.parentSet = parentSet;
        this.removed = new TreeSet<> (Stats.getDefaultComparator());
    }

    @Override
    public void setBaseScore(Stat ability, Integer value) {
        if (value == null) this.removeBaseScore(ability);
        else super.setBaseScore(ability, value);
        this.removed.remove(ability);
        this.reloadScores();
    }

    @Override
    public void removeBaseScore(Stat ability) {
        super.removeBaseScore(ability);
        base_scores.remove(ability);
        modifier_values.remove(ability);
        scores.remove(ability);
        removed.add(ability);
        this.reloadScores();
    }

    @Override
    public void resetBaseScore(Stat ability) {
        this.removed.remove(ability);
        this.base_scores.remove(ability);
        this.reloadScores();
    }

    @Override
    public int getBaseScore(Stat ability) {
        if (removed.contains(ability)) return getDefaultScore();
        else if (base_scores.containsKey(ability)) return base_scores.get(ability);
        return (parentSet.getScore(ability));
    }

    @Override
    public int getScore(Stat ability) {
        if (removed.contains(ability)) return getDefaultScore();
        if (base_scores.containsKey(ability)) return scores.get(ability);
        else return (parentSet.getScore(ability) + this.getModifierTotal(ability));
    }

    @Override
    public int getDefaultScore() {
        return parentSet.getDefaultScore();
    }

    @Override
    public Set<Stat> getSpecified() {
        Set<Stat> a = new TreeSet<>(Stats.getDefaultComparator());
        a.addAll(parentSet.getSpecified());
        a.addAll(super.getSpecified());
        a.removeAll(removed);
        return a;
    }

    @Override
    public void reloadScores() {
        if (parent != null && parent.getStatSet() != parentSet) parentSet = parent.getStatSet();
        // System.out.println(getSpecified());
        super.reloadScores();
    }
}
