package dungeonmanager.event;

import java.util.*;
import java.util.function.Consumer;

public class  EventListener {

    private static final Deque<Event> events = new LinkedList<Event> ();
    private static final Map<String, List<Consumer<Event>>> handlers = new TreeMap<> ();

    public static <T extends Event> void addHandler(EventType type, Consumer<T> handler) {
        String id = type.getID();
        if (!handlers.containsKey(id)) {
            handlers.put(id, new ArrayList<Consumer<Event>>());
        }
        handlers.get(id).add((Consumer<Event>) handler);
    }

    public static <T extends Event> void removeHandler(EventType event_type, Consumer<T> handler) {
        String id = event_type.getID();
        if (handlers.containsKey(id)) {
            handlers.get(id).remove(handler);
        }
    }

    public static void handleEvents() {
        for (Event event = events.poll(); !events.isEmpty(); event = events.poll()) {
            if (event == null) continue;
            String id = event.type.getID();
            if (handlers.containsKey(id)) {
                for (Consumer<Event> handler: handlers.get(id)) {
                    handler.accept(event);
                }
            }
        }
    }

    public static <T extends Event> void fire(T event) {
        events.add(event);
    }
}
