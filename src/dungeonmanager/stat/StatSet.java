package dungeonmanager.stat;

import java.util.Set;

/**
 * Interface for read-only access to a set of defined stat and their values.
 */
public interface StatSet {

    public int getValue(Stat stat);
    public Set<Stat> getSpecifiedStats();
}
