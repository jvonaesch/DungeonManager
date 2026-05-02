package dungeonmanager.library;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class HashLibrary<T> implements Library<T> {

    protected ConcurrentHashMap<String, T> entries;
    protected Set<String> locked;

    public HashLibrary() {
        this.entries = new ConcurrentHashMap<>();
        this.locked = new HashSet<>();
    }

    @Override
    public void putLocked(String id, T element) {
        entries.put(id, element);
        locked.add(id);
    }

    @Override
    public void putLocked(String id, Supplier<T> supplier) {
        this.putLocked(id, supplier.get());
    }

    @Override
    public void putOwned(String id, T element) {
        entries.put(id, element);
    }

    @Override
    public void putOwned(String id, Supplier<T> supplier) {
        this.putOwned(id, supplier.get());
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
    public Map<String, T> getAll() {
        return new HashMap<> (entries);
    }

    @Override
    public boolean containsKey(String creatureId) {
        return entries.containsKey(creatureId);
    }

    @Override
    public T unregister(String creatureId) {
        return entries.remove(creatureId);
    }

    @Override
    public Set<String> getOwnedKeys() {
        Set<String> items = new HashSet<>(entries.keySet());
        items.removeAll(locked);
        return items;
    }
}
