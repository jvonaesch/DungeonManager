package dungeonmanager.registry;

import java.util.TreeMap;
import java.util.function.Supplier;

public class TreeRegistry<T> implements Registry<T> {

    private TreeMap<String, T> map;

    public TreeRegistry() {
        this.map = new TreeMap<>();
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
}
