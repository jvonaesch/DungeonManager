package dungeonmanager.ability;

public enum StandardAbility implements Ability {
    STR("standard:dungeonmanager.ability:strength", "STR", "strength"),
    CON("standard:dungeonmanager.ability:constitution", "CON", "constitution"),
    DEX("standard:dungeonmanager.ability:dexterity", "DEX", "dexterity"),
    INT("standard:dungeonmanager.ability:intelligence", "INT", "intelligence"),
    WIS("standard:dungeonmanager.ability:wisdom", "WIS", "wisdom"),
    CHA("standard:dungeonmanager.ability:charisma", "CHA", "charisma");

    private String name;
    private String short_name;
    private String id;

    private StandardAbility(String id, String short_name, String name) {
        this.id = id;
        this.name = name;
        this.short_name = short_name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getShortName() {
        return short_name;
    }

    @Override
    public String getID() {
        return id;
    }
}
