package dungeonmanager.stat;


public class Stat implements IStat {

    private final String originIdentifier;
    private final String name;
    private final String ID;
    private final String type;
    private final int default_value;

    public Stat(String ID, String name, String type, int default_value, String originIdentifier) {
        this.originIdentifier = originIdentifier;
        this.name = name;
        this.ID = ID;
        this.type = type;
        this.default_value = default_value;
    }

    public Stat(String ID, String name, String type, int default_value) {
        this(ID, name, type, default_value, "custom:dungeonmanager.stat." + type + ':' + ID);
    }

    public Stat(String ID, String name, String type) {
        this(ID, name, type, 0, "custom:dungeonmanager.stat." + type + ':' + ID);
    }

    public Stat(String ID, String name) {
        this(ID, name, "base_stat", 0,"custom:dungeonmanager.stat:" + ID);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getOriginIdentifier() {
        return originIdentifier;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getDefaultValue() {
        return default_value;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public String toString() {
        return this.ID;
    }
}
