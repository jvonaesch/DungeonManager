package dungeonmanager.stat;


public enum StandardStat implements IStat {
    STR("STR", "strength", "ability"),
    CON("CON", "constitution", "ability"),
    DEX("DEX", "dexterity", "ability"),
    INT("INT", "intelligence", "ability"),
    WIS("WIS", "wisdom", "ability"),
    CHA("CHA", "charisma", "ability"),

    MAX_HP("MAX_HP", "maximum health points");

    private final String name;
    private final String ID;
    private final String origin_identifier;
    private final String type;
    private final int default_value;

    StandardStat(String ID, String name, String type, int default_value, String origin_identifier) {
        this.origin_identifier = origin_identifier;
        this.name = name;
        this.ID = ID;
        this.type = type;
        this.default_value = default_value;
    }

    StandardStat(String ID, String name, String type, int default_value) {
        this(ID, name, type, default_value, "standard:dungeonmanager.stat." + type + ':' + ID);
    }

    StandardStat(String ID, String name, String type) {
        this(ID, name, type, type.equals("ability") ? 10 : 0);
    }

    StandardStat(String ID, String name) {
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
    public String getId() {
        return ID;
    }
}
