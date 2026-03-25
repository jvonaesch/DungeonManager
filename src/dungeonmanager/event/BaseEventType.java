package dungeonmanager.event;

public enum BaseEventType implements EventType {

    DEFAULT("base:dungeonmanager.event:default"),
    PROFICIENCY_SET_MODIFIER_UPDATE("base:dungeonmanager.event:proficiency:prof_set_modifier_update"),
    UPDATE_EVENT("base:dungeonmanager.event:update");


    private final String id;

    private BaseEventType(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }
}
