package dungeonmanager.stat;

import java.util.Map;
import java.util.Set;

/**
 * Singleton class representing the default set of stat for a creature.
 */
public class DefaultStatSet implements StatSet {

    protected Map<String, IStat> specified;
    private static DefaultStatSet INSTANCE;

    private DefaultStatSet(Set<IStat> specified) {
        this.specified = specified.stream().collect(java.util.stream.Collectors.toMap(IStat::getId, stat -> stat));
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
    public Integer getValue(String statId) {
        if (specified.containsKey(statId)) return specified.get(statId).getDefaultValue();
        return null;
    }

    @Override
    public Set<String> getSpecifiedStats() {
        return specified.keySet();
    }
}
