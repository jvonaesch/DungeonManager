package dungeonmanager.registry;

public interface Registry <T> {

    public void register(String ID, T element);
    public T get(String ID);
}
