package dungeonmanager.ability;

public enum StandardAbility implements Ability {
    STR("STR", "strength", "standard:dungeonmanager.ability:strength"),
    CON("CON", "constitution", "standard:dungeonmanager.ability:constitution"),
    DEX("DEX", "dexterity", "standard:dungeonmanager.ability:dexterity"),
    INT("INT", "intelligence", "standard:dungeonmanager.ability:intelligence"),
    WIS("WIS", "wisdom", "standard:dungeonmanager.ability:wisdom"),
    CHA("CHA", "charisma", "standard:dungeonmanager.ability:charisma");

    private String name;
    private String id;
    private String origin_identifier;

    private StandardAbility(String id, String name, String origin_identifier) {
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
