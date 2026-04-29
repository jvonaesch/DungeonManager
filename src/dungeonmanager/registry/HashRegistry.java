package dungeonmanager.registry;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class HashRegistry<T> implements Registry<T> {

    private ConcurrentHashMap<String, T> map;

    public HashRegistry() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public void register(String ID, T element) {
        map.put(ID, element);
    }

    @Override
    public void register(String ID, Supplier<T> supplier) {
        this.register(ID, supplier.get());
    }

    @Override
    public T get(String ID) {
        return map.get(ID);
    }

    @Override
    public int getSize() {
        return map.size();
    }

    @Override
    public Set<String> getAllKeys() {
        return map.keySet();
    }
}
