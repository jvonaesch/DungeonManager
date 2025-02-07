package registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class LazyRegistry<T extends RegistryElement> {

    private final Map<String, T> registerMap;

    public LazyRegistry() {
        this.registerMap = new ConcurrentHashMap<String, T>();
    }

    public void register(T element) {
        this.registerMap.put(element.getID(), element);
    }

    public T get(String ID) {
        return registerMap.get(ID);
    }

    public class LazyRegistryObject implements RegistryObject <T> {

        private T item;
        private Supplier <T> supplier;
        private final String ID;

        public LazyRegistryObject (String ID, Supplier<T> supplier) {
            this.ID = ID;
            this.supplier = supplier;
        }

        public T get() {
            if (item == null) item = supplier.get();
            return item;
        }

        @Override
        public String getID() {
            return this.ID;
        }
    }
}
