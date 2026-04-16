package dungeonmanager.creature;

import dungeonmanager.stat.*;
import dungeonmanager.feature.FeatureSet;

public class Creature implements HasStatSet {

    private String name;
    private CreatureType type;
    public final ModifiableStatSet stats;
    public final FeatureSet feature;

    public Creature(String name, CreatureType type) {
        this.name = name;
        this.type = type;
        this.stats = new DefaultedStatSet(type);
        this.feature = new FeatureSet(this.stats);
    }

    public Creature(String name) {
        this(name, IntegratedCreatureType.DEFAULT);
    }

    public String toString() {
        return "\"" + name + "\"\n "+ StatSets.toString(stats);
    }

    @Override
    public ModifiableStatSet getStatSet() {
        return stats;
    }
}
