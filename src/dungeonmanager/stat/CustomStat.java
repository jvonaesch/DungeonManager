package dungeonmanager.stat;

/**
 * User-defined stat implementation for custom attributes.
 * Allows creation of stat not covered by the standard set.
 * Custom stat are registered with the global stat registry for lookup by ID.
 * 
 * @see dungeonmanager.stat.StandardStat for predefined stat
 */
public class CustomStat implements Stat {

    private String originIdentifier;
    private String name;
    private String ID;
    private String type;
    private int default_value;

    public CustomStat(String ID, String name, String type, int default_value, String originIdentifier) {
        this.originIdentifier = originIdentifier;
        this.name = name;
        this.ID = ID;
        this.type = type;
        this.default_value = default_value;
    }

    public CustomStat(String ID, String name, String type, int default_value) {
        this(ID, name, type, default_value, "custom:dungeonmanager.stat." + type + ':' + ID);
    }

    public CustomStat(String ID, String name, String type) {
        this(ID, name, type, 0, "custom:dungeonmanager.stat." + type + ':' + ID);
    }

    public CustomStat(String ID, String name) {
        this(ID, name, "base_stat", 0,"custom:dungeonmanager.stat:" + ID);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getID() {
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
