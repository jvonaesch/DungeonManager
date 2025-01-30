package creature;

import ability.*;

public enum IntegratedEntityType implements EntityType {
    OWLBEAR("base:creature:owlbear", "owlbear");

    private String id;
    private String name;
    private ModifiableAbilitySet abilitySet;

    private IntegratedEntityType(String id, String name) {
        this.id = id;
        this.name = name;
        this.abilitySet = new DefaultedAbilitySet(StandardAbilitySet.DEFAULT());
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
