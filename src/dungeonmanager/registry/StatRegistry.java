package dungeonmanager.registry;

import dungeonmanager.stat.Stat;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class StatRegistry extends HashRegistry<Stat> {

    private final Map<String, Integer> defaultValues;

    public StatRegistry () {
        super();
        this.defaultValues = new TreeMap<>();
    }

    public void register(String ID, Stat stat) {
        super.register(ID, stat);
        defaultValues.put(ID, stat.getDefaultValue());
    }

    public Map<String, Integer> getDefaultValues() {
        return Collections.unmodifiableMap(defaultValues);
    }
}
