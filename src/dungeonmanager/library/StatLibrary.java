package dungeonmanager.library;

import dungeonmanager.stat.Stat;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class StatLibrary extends HashLibrary<Stat> {

    private final Map<String, Integer> defaultValues;

    public StatLibrary() {
        super();
        this.defaultValues = new TreeMap<>();
    }

    public void putLocked(String id, Stat stat) {
        super.putLocked(id, stat);
        defaultValues.put(id, stat.getDefaultValue());
    }

    public Map<String, Integer> getDefaultValues() {
        return Collections.unmodifiableMap(defaultValues);
    }

    public Map<String, Stat> getAll() {
        return Collections.unmodifiableMap(entries);
    }
}
