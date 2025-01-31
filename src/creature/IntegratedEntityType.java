package creature;

import ability.*;

public enum IntegratedEntityType implements EntityType {
    DEFAULT("base:creature:default", "default"),
    OWLBEAR("base:creature:owlbear", "owlbear", 20, 12, 17, 3, 12, 7);

    private String id;
    private String name;
    private ModifiableAbilitySet abilitySet;

    private IntegratedEntityType(String id, String name) {
        this.id = id;
        this.name = name;
        this.abilitySet = new DefaultedAbilitySet(StandardAbilitySet.DEFAULT());
    }

    private IntegratedEntityType(String id, String name, int STR, int DEX, int CON, int INT, int WIS, int CHA) {
        this(id, name);
        this.abilitySet.setBaseScore(StandardAbility.STR, STR);
        this.abilitySet.setBaseScore(StandardAbility.DEX, DEX);
        this.abilitySet.setBaseScore(StandardAbility.CON, CON);
        this.abilitySet.setBaseScore(StandardAbility.INT, INT);
        this.abilitySet.setBaseScore(StandardAbility.WIS, WIS);
        this.abilitySet.setBaseScore(StandardAbility.CHA, CHA);
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AbilitySet getAbilitySet() {
        return abilitySet;
    }
}
