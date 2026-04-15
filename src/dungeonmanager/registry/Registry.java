package dungeonmanager.registry;

import java.util.function.Supplier;

public interface Registry<T> {
    void register(String ID, T element);

    void register(String ID, Supplier<T> supplier);

    T get(String ID);
}
