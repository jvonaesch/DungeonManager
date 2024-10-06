package event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class  EventListener {

    private static final Stack<Event> events = new Stack<Event> ();
    private static final HashMap<String, List<Consumer<Event>>> handlers =
            new HashMap<String, List<Consumer<Event>>> ();

    public static <T extends Event> void addHandler(String event_type, Consumer<Event> handler) {
        if (!handlers.containsKey(event_type)) handlers.put(event_type, new ArrayList<Consumer<Event>>());
        handlers.get(event_type).add(handler);
    }
    public static <T extends Event> void removeHandler(Class<T> event_type, Consumer<T> handler) {
        if (handlers.containsKey(event_type)) {
            handlers.get(event_type).remove(handler);
        }
    }

    public static void handleEvents() {
        for (Event event: events) for (String type: event.type_ids) {
            if (handlers.containsKey(type)) for (Consumer<Event> handler: handlers.get(type)) {
                handler.accept(event);
            }
        }
    }
    public static <T extends Event> void fire(T event) {
        events.add(event);
    }
}
