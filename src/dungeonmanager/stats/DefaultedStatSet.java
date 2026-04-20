package dungeonmanager.stats;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * StatSet implementation that inherits values from a parent stat set.
 * Supports stat removal and reset operations, falling back to parent values when needed.
 */
public class DefaultedStatSet extends ModifiableStatSet {

    private HasStatSet parent;
    private StatSet parentSet;
    private final Set<Stat> removed;

    public DefaultedStatSet(HasStatSet parent) {
        this(parent.getStatSet());
        this.parent = parent;
    }

    public DefaultedStatSet(StatSet parentSet) {
        super();
        this.parentSet = parentSet;
        this.removed = new HashSet<>();
    }

    /**
     * Sets the base value for a stat, <b>marking it as not removed</b>.
     * @param stat the stat to modify
     * @param value the new base value, or null to remove
     */
    @Override
    public void setBaseValue(Stat stat, Integer value) {
        if (value == null) this.removeBaseValue(stat);
        else super.setBaseValue(stat, value);
        this.removed.remove(stat);
        this.reloadValues();
    }

    /**
     * Removes the base value for a stat and marks it as removed.
     * Removed stats will not fall back to parent values.
     * @param stat the stat to remove
     */
    @Override
    public void removeBaseValue(Stat stat) {
        super.removeBaseValue(stat);
        values.remove(stat);
        removed.add(stat);
        this.reloadValues();
    }

    /**
     * Resets a stat to parent value.
     * @param stat the stat to reset
     */
    @Override
    public void resetBaseValue(Stat stat) {
        this.removed.remove(stat);
        this.base_values.remove(stat);
        this.reloadValues();
    }

    @Override
    public int getBaseValue(Stat stat) {
        if (removed.contains(stat)) return parentSet.getValue(stat);
        else if (base_values.containsKey(stat)) return base_values.get(stat);
        return (parentSet.getValue(stat));
    }
    
    @Override
    public int getValue(Stat stat) {
        if (removed.contains(stat)) return parentSet.getValue(stat);
        if (base_values.containsKey(stat)) return values.get(stat);
        else return (parentSet.getValue(stat) + this.getModifierTotal(stat));
    }

    @Override
    public Set<Stat> getSpecifiedStats() {
        Set<Stat> a = new HashSet<>(parentSet.getSpecifiedStats());
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
