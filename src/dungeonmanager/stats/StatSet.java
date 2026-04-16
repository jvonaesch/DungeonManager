package dungeonmanager.stats;

import java.util.Set;

public interface StatSet {
    public int getValue(Stat stat);
    // public int getDefaultValue();

    public Set<Stat> getSpecifiedStats();
}
