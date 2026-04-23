package dungeonmanager.stat;

import java.util.HashSet;
import java.util.Set;

/**
 * StatSet implementation that inherits values from a parent stat set.
 * Supports stat removal and reset operations, falling back to parent values when needed.
 */
public class DefaultedStatSet extends ModifiableStatSet {

    private HasStatSet parent;
    private StatSet parentSet;
    private final Set<String> removed;

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
     *
     * @param statId the stat to modify
     * @param value  the new base value, or null to remove
     */
    @Override
    public void setBaseValue(String statId, Integer value) {
        if (value == null) this.removeBaseValue(statId);
        else super.setBaseValue(statId, value);
        this.removed.remove(statId);
        this.reloadValues();
    }

    /**
     * Removes the base value for a stat and marks it as removed.
     * Removed stat will not fall back to parent values.
     *
     * @param statId the stat to remove
     */
    @Override
    public void removeBaseValue(String statId) {
        super.removeBaseValue(statId);
        values.remove(statId);
        removed.add(statId);
        this.reloadValues();
    }

    @Override
    public void resetBaseValue(Stat stat) {
        this.removed.remove(stat.getId());
        this.baseValues.remove(stat.getId());
        this.reloadValues();
    }

    @Override
    public Integer getBaseValue(String statId) {
        if (removed.contains(statId)) return parentSet.getValue(statId);
        else if (baseValues.containsKey(statId)) return baseValues.get(statId);
        return (parentSet.getValue(statId));
    }

    @Override
    public Integer getValue(String statId) {
        if (removed.contains(statId)) return parentSet.getValue(statId);
        if (baseValues.containsKey(statId)) return values.get(statId);
        else return (parentSet.getValue(statId) + this.getModifierTotal(statId));
    }

    @Override
    public Set<String> getSpecifiedStats() {
        Set<String> a = new HashSet<>(parentSet.getSpecifiedStats());
        a.addAll(super.getSpecifiedStats());
        a.removeAll(removed);
        return a;
    }

    @Override
    public void reloadValues() {
        if (parent != null && parent.getStatSet() != parentSet) parentSet = parent.getStatSet();
        super.reloadValues();
    }

    public void changeParent(HasStatSet newParent) {
        this.parent = newParent;
        this.parentSet = newParent.getStatSet();
        this.reloadValues();
    }

    @Deprecated
    public void changeParent(StatSet newParentSet) {
        this.parent = null;
        this.parentSet = newParentSet;
        this.reloadValues();
    }
}
