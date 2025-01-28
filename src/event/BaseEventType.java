package event;

public enum BaseEventType implements EventType {

    DEFAULT("base:event:default"),
    PROFICIENCY_SET_MODIFIER_UPDATE("base:event:proficiency:prof_set_modifier_update"),
    UPDATE_EVENT("base:event:update");


    private final String id;

    private BaseEventType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
