package event;

import java.util.*;
import java.util.function.Consumer;

public class  EventListener {

    private static final Deque<Event> events = new LinkedList<Event> ();
    private static final TreeMap<BaseEventType, List<Consumer<Event>>>
            handlers = new TreeMap<BaseEventType, List<Consumer<Event>>> ();

    public static <T extends Event> void addHandler(BaseEventType type, Consumer<T> handler) {
        if (!handlers.containsKey(type)) handlers.put(type, new ArrayList<Consumer<Event>>());
        handlers.get(type).add((Consumer<Event>) handler);
    }
    public static <T extends Event> void removeHandler(Class<T> event_type, Consumer<T> handler) {
        if (handlers.containsKey(event_type)) {
            handlers.get(event_type).remove(handler);
        }
    }

    public static void handleEvents() {
        for (Event event = events.poll(); !events.isEmpty(); event = events.poll()) {
            if (handlers.containsKey(event.type)) {
                for (Consumer<Event> handler: handlers.get(event.type)) {
                    handler.accept(event);
                }
            }
        }
    }
    public static <T extends Event> void fire(T event) {
        events.add(event);
    }
}
