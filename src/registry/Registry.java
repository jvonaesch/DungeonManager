package registry;

public interface Registry <T> {

    public void register(T element);
    public T get(String ID);
}
