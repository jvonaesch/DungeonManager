package dungeonmanager.creature;

import dungeonmanager.ability.*;
import dungeonmanager.feature.FeatureSet;

public class Creature implements HasAbilitySet {

    private String name;
    private CreatureType type;
    public final ModifiableAbilitySet ability;
    public final FeatureSet feature;

    public Creature(String name, CreatureType type) {
        this.name = name;
        this.type = type;
        this.ability = new DefaultedAbilitySet(type);
        this.feature = new FeatureSet();
    }

    public Creature(String name) {
        this(name, IntegratedCreatureType.DEFAULT);
    }

    public String toString() {
        return "\"" + name + "\"\n "+AbilitySets.toString(ability);
    }

    @Override
    public ModifiableAbilitySet getAbilitySet() {
        return ability;
    }
}
