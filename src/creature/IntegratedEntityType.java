package creature;

import ability.AbilitySet;
import ability.BaseAbilitySet;
import ability.DefaultedAbilitySet;

public enum IntegratedEntityType implements EntityType {
    DEFAULT()
    OWLBEAR("base:creature:owlbear", "owlbear");


    private String id;
    private String name;

    private IntegratedEntityType(String id, String name) {
        this.id = id;
        this.name = name;
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

        //TODO: Implement base score set for entity type
        return new DefaultedAbilitySet();
    }
}
