package dungeonmanager.stats;

import java.util.Set;
import java.util.TreeSet;

public class DefaultStatSet implements StatSet {

    protected Set<Stat> specified;
    private static DefaultStatSet defaultInstance;

    public DefaultStatSet(Set<Stat> specified) {
        this.specified = new TreeSet<> (Stats.getDefaultComparator());
        this.specified.addAll(specified);
    }

    public static DefaultStatSet get() {
        if (defaultInstance == null) {
            defaultInstance = new DefaultStatSet(Set.of(StandardStat.values()));
        }
        return defaultInstance;
    }

    @Override
    public int getValue(Stat stat) {
        return stat.getDefaultValue();
    }

    @Override
    public Set<Stat> getSpecifiedStats() {
        return specified;
    }
}
