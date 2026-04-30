package dungeonmanager.registry;

import dungeonmanager.session.SessionHandle;

import java.util.Set;
import java.util.function.Supplier;

public interface Registry<T> {
    void register(String ID, T element);

    void register(String ID, Supplier<T> supplier);

    T get(String ID);

    int getSize();

    Set<String> getAllKeys();

    boolean containsKey(String creatureId);

    T unregister(String creatureId);
}
