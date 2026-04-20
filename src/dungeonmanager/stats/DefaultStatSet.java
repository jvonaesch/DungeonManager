package dungeonmanager.stats;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton class representing the default set of stats for a creature.
 */
public class DefaultStatSet implements StatSet {

    protected Set<Stat> specified;
    private static DefaultStatSet INSTANCE;

    private DefaultStatSet(Set<Stat> specified) {
        this.specified = new HashSet<>(specified);
    }

    /**
     * Singleton constructor for DefaultStatSet
     * @return the default stat set instance
     */
    public static DefaultStatSet get() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultStatSet(Set.of(StandardStat.values()));
        }
        return INSTANCE;
    }

    @Override
    public int getValue(Stat stat) {
        return stat.getDefaultValue();
    }

    @Override
    public Set<Stat> getSpecifiedStats() {
        return Set.of(StandardStat.values());
    }
}
