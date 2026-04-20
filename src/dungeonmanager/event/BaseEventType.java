package dungeonmanager.event;

public enum BaseEventType implements EventType {

    DEFAULT("dungeonmanager.event:default"),
    FEATURE_UPDATE("dungeonmanager.event:feature_update");

    private final String id;

    BaseEventType(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }
}
