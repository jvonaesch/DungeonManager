package event;

import java.util.ArrayList;

public class Event {

    public final ArrayList<String> type_ids;

    public Event() {
        type_ids = new ArrayList<String> ();
        type_ids.add("event");
    }
}
