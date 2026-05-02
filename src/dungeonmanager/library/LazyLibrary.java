package dungeonmanager.library;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyLibrary<T> implements Library<T> {

    private final Map<String, LazyLibraryItem> libraryMap;
    private final Set<String> locked = new HashSet<>();

    public LazyLibrary() {
        this.libraryMap = new ConcurrentHashMap<String, LazyLibraryItem>();
    }

    @Override
    public void putLocked(String id, T element) {
        LazyLibraryItem regObj = new LazyLibraryItem(id, element);
        locked.add(id);
        this.libraryMap.put(id, regObj);
    }

    @Override
    public void putLocked(String id, Supplier<T> supplier) {
        LazyLibraryItem regObj = new LazyLibraryItem(id, supplier);
        locked.add(id);
        this.libraryMap.put(id, regObj);
    }

    @Override
    public void putOwned(String id, T element) {
        LazyLibraryItem regObj = new LazyLibraryItem(id, element);
        this.libraryMap.put(id, regObj);
    }

    @Override
    public void putOwned(String id, Supplier<T> supplier) {
        LazyLibraryItem regObj = new LazyLibraryItem(id, supplier);
        this.libraryMap.put(id, regObj);
    }

    @Override
    public T get(String ID) {
        LazyLibraryItem item = libraryMap.get(ID);
        if (item == null) return null;
        return item.get();
    }

    @Override
    public int getSize() {
        return libraryMap.size();
    }

    @Override
    public Set<String> getAllKeys() {
        return libraryMap.keySet();
    }

    @Override
    public boolean containsKey(String creatureId) {
        return libraryMap.containsKey(creatureId);
    }

    @Override
    public T unregister(String creatureId) {
        LibraryItem<T> item = libraryMap.remove(creatureId);
        return item == null ? null : item.get();
    }

    @Override
    public Set<String> getOwnedKeys() {
        Set<String> items = new HashSet<>(libraryMap.keySet());
        items.removeAll(locked);
        return items;
    }

    private class LazyLibraryItem implements LibraryItem<T> {

        private final AtomicReference<T> item;
        private final Supplier <T> supplier;
        private final String ID;

        public boolean inUse = false;

        public LazyLibraryItem(String ID, T item) {
            this.ID = ID;
            this.item = new AtomicReference<>();
            this.item.set(item);
            this.supplier = null;
        }

        public LazyLibraryItem(String ID, Supplier<T> supplier) {
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
