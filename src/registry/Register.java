package registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Register <T extends RegistryElement> {

    private final Map<String, T> registerMap;

    public Register () {
        this.registerMap = new ConcurrentHashMap<String, T>();
    }

    public void register(T element) {
        this.registerMap.put(element.getID(), element);
    }

    public T get(String ID) {
        return registerMap.get(ID);
    }
}
