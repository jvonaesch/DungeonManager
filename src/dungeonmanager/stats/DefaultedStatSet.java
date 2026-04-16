package dungeonmanager.stats;

import java.util.Set;
import java.util.TreeSet;

public class DefaultedStatSet extends ModifiableStatSet {

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
    public void setBaseValue(Stat stat, Integer value) {
        if (value == null) this.removeBaseValue(stat);
        else super.setBaseValue(stat, value);
        this.removed.remove(stat);
        this.reloadValues();
    }

    @Override
    public void removeBaseValue(Stat stat) {
        super.removeBaseValue(stat);
        base_values.remove(stat);
        modifier_values.remove(stat);
        scores.remove(stat);
        removed.add(stat);
        this.reloadValues();
    }

    @Override
    public void resetBaseValue(Stat stat) {
        this.removed.remove(stat);
        this.base_values.remove(stat);
        this.reloadValues();
    }

    @Override
    public int getBaseValue(Stat stat) {
        if (removed.contains(stat)) return this.getDefaultValue(stat);
        else if (base_values.containsKey(stat)) return base_values.get(stat);
        return (parentSet.getValue(stat));
    }

    @Override
    public int getValue(Stat stat) {
        if (removed.contains(stat)) return this.getDefaultValue(stat);
        if (base_values.containsKey(stat)) return scores.get(stat);
        else return (parentSet.getValue(stat) + this.getModifierTotal(stat));
    }

    public int getDefaultValue(Stat stat) {
        return parentSet.getValue(stat);
    }

    @Override
    public Set<Stat> getSpecifiedStats() {
        Set<Stat> a = new TreeSet<>(Stats.getDefaultComparator());
        a.addAll(parentSet.getSpecifiedStats());
        a.addAll(super.getSpecifiedStats());
        a.removeAll(removed);
        return a;
    }

    @Override
    public void reloadValues() {
        if (parent != null && parent.getStatSet() != parentSet) parentSet = parent.getStatSet();
        super.reloadValues();
    }
}
