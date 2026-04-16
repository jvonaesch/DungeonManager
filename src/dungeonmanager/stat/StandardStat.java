package dungeonmanager.stat;

public enum StandardStat implements Stat {
    STR("STR", "strength", "standard:dungeonmanager.stat:strength"),
    CON("CON", "constitution", "standard:dungeonmanager.stat:constitution"),
    DEX("DEX", "dexterity", "standard:dungeonmanager.stat:dexterity"),
    INT("INT", "intelligence", "standard:dungeonmanager.stat:intelligence"),
    WIS("WIS", "wisdom", "standard:dungeonmanager.stat:wisdom"),
    CHA("CHA", "charisma", "standard:dungeonmanager.stat:charisma");

    private String name;
    private String id;
    private String origin_identifier;

    private StandardStat(String id, String name, String origin_identifier) {
        this.origin_identifier = origin_identifier;
        this.name = name;
        this.id = id;
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
    public String getID() {
        return id;
    }
}
