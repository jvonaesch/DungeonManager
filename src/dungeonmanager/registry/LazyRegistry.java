package dungeonmanager.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyRegistry<T> implements Registry<T> {

    private final Map<String, LazyRegistryObject> registerMap;

    public LazyRegistry() {

        this.registerMap = new ConcurrentHashMap<String, LazyRegistryObject>();
    }

    @Override
    public void register(String ID, T element) {
        LazyRegistryObject regObj = new LazyRegistryObject(ID, element);
        this.registerMap.put(ID, regObj);
    }

    @Override
    public void register(String ID, Supplier<T> supplier) {
        LazyRegistryObject regObj = new LazyRegistryObject(ID, supplier);
        this.registerMap.put(ID, regObj);
    }

    @Override
    public T get(String ID) {
        LazyRegistryObject item = registerMap.get(ID);
        if (item == null) throw new IllegalArgumentException("item \"%s\" not found".formatted(ID));
        return item.get();
    }

    private class LazyRegistryObject implements RegistryObject <T> {

        private AtomicReference<T> item;
        private Supplier <T> supplier;
        private final String ID;

        public boolean inUse = false;

        public LazyRegistryObject (String ID, T item) {
            this.ID = ID;
            this.item = new AtomicReference<>();
            this.item.set(item);
            this.supplier = null;
        }

        public LazyRegistryObject (String ID, Supplier<T> supplier) {
            this.ID = ID;
            this.item = new AtomicReference<>();
            this.supplier = supplier;
        }

        public T get() {
            if (item.get() == null) {
                if (supplier == null) {
                    //registerMap.remove(ID);
                    throw new IllegalStateException("Registry object is empty"); // and will be REMOVED
                }
                item.compareAndSet(null, supplier.get());
            }
            return item.get();
        }

        @Override
        public String getID() {
            return this.ID;
        }
    }
}
