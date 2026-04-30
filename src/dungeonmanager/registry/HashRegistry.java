package dungeonmanager.registry;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class HashRegistry<T> implements Registry<T> {

    protected ConcurrentHashMap<String, T> entries;

    public HashRegistry() {
        this.entries = new ConcurrentHashMap<>();
    }

    @Override
    public void register(String ID, T element) {
        entries.put(ID, element);
    }

    @Override
    public void register(String ID, Supplier<T> supplier) {
        this.register(ID, supplier.get());
    }

    @Override
    public T get(String ID) {
        return entries.get(ID);
    }

    @Override
    public int getSize() {
        return entries.size();
    }

    @Override
    public Set<String> getAllKeys() {
        return entries.keySet();
    }

    @Override
    public boolean containsKey(String creatureId) {
        return entries.containsKey(creatureId);
    }

    @Override
    public T unregister(String creatureId) {
        return entries.remove(creatureId);
    }
}
