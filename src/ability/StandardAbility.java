package ability;

public enum StandardAbility implements Ability {
    STR("standard:ability:strength", "STR", "strength"),
    CON("standard:ability:constitution", "CON", "constitution"),
    DEX("standard:ability:dexterity", "DEX", "dexterity"),
    INT("standard:ability:intelligence", "INT", "intelligence"),
    WIS("standard:ability:wisdom", "WIS", "wisdom"),
    CHA("standard:ability:strength", "CHA", "charisma");

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
