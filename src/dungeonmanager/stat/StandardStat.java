package dungeonmanager.stat;

/**
 * Enumeration of standard stat provided by the system.
 * Provides predefined stat with standard names, IDs, and default values.
 * All standard stat are automatically registered with the global stat registry.
 * 
 * @see dungeonmanager.stat.CustomStat for user-defined stat
 * @see dungeonmanager.DungeonManagerApp#initialize() for registration
 */
public enum StandardStat implements Stat {
    STR("STR", "strength", "ability"),
    CON("CON", "constitution", "ability"),
    DEX("DEX", "dexterity", "ability"),
    INT("INT", "intelligence", "ability"),
    WIS("WIS", "wisdom", "ability"),
    CHA("CHA", "charisma", "ability"),

    MAX_HP("MAX_HP", "maximum health points");

    private String name;
    private String ID;
    private String origin_identifier;
    private String type;
    private int default_value;

    private StandardStat(String ID, String name, String type, int default_value, String origin_identifier) {
        this.origin_identifier = origin_identifier;
        this.name = name;
        this.ID = ID;
        this.type = type;
        this.default_value = default_value;
    }

    private StandardStat(String ID, String name, String type, int default_value) {
        this(ID, name, type, default_value, "standard:dungeonmanager.stat." + type + ':' + ID);
    }

    private StandardStat(String ID, String name, String type) {
        this(ID, name, type, type.equals("ability") ? 10 : 0);
    }

    private StandardStat(String ID, String name, int default_value) {
        this(ID, name, "base_stat", default_value);
    }

    private StandardStat(String ID, String name) {
        this(ID, name, "base_stat");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginIdentifier() {
        return origin_identifier;
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
    public String getID() {
        return ID;
    }
}
