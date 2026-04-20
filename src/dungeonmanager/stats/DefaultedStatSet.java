package dungeonmanager.stats;

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
        this.parentSet = parentSet;
        this.removed = new TreeSet<> (Stats.getDefaultComparator());
    }

    /**
     * Sets the base value for a stat, marking it as not removed.
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
        base_values.remove(stat);
        modifier_values.remove(stat);
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

    /**
     * Gets the final value of a stat, respecting removal status and inheritance.
     * @param stat the stat to query
     * @return final value, or parent value if not locally defined and not removed
     */
    @Override
    public int getValue(Stat stat) {
        if (removed.contains(stat)) return parentSet.getValue(stat);
        if (base_values.containsKey(stat)) return values.get(stat);
        else return (parentSet.getValue(stat) + this.getModifierTotal(stat));
    }

    /**
     * Gets the default value from the parent stat set.
     * @param stat the stat to query
     * @return parent's value for this stat
     */
    public int getDefaultValue(Stat stat) {
        return parentSet.getValue(stat);
    }

    /**
     * Gets all specified stats, including parent stats but excluding removed ones.
     * @return combined set of local and inherited stats
     */
    @Override
    public Set<Stat> getSpecifiedStats() {
        Set<Stat> a = new TreeSet<>(Stats.getDefaultComparator());
        a.addAll(parentSet.getSpecifiedStats());
        a.addAll(super.getSpecifiedStats());
        a.removeAll(removed);
        return a;
    }

    /**
     * Recalculates values and updates parent reference if needed.
     * @see dungeonmanager.stats.ModifiableStatSet#reloadValues() for base recalculation
     */
    @Override
    public void reloadValues() {
        if (parent != null && parent.getStatSet() != parentSet) parentSet = parent.getStatSet();
        super.reloadValues();
    }
}
