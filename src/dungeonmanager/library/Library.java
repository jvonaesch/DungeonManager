package dungeonmanager.library;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface Library<T> {

    /**
     * Add an element to the library whose ID is not owned by the library itself
     * @param id
     * @param element
     */
    void putLocked(String id, T element);

    void putLocked(String id, Supplier<T> supplier);

    void putOwned(String id, T element);

    void putOwned(String id, Supplier<T> supplier);

    T get(String id);

    int getSize();

    Set<String> getAllKeys();

    default Map<String, T> getAll(Set<String> keys) {
        Map<String, T> items = new HashMap<>();
        for (String key: keys) items.put(key, get(key));
        return items;
    }

    /**
     * @return a map of all items (evaluates all items)
     */
    default Map<String, T> getAll() {
        return getAll(getAllKeys());
    }

    boolean containsKey(String creatureId);

    /**
     * Remove an element from the library
     * @param creatureId
     * @return
     */
    T unregister(String creatureId);

    Set<String> getOwnedKeys();

    default Map<String, T> getOwned() {
        return this.getAll(getOwnedKeys());
    }
}
