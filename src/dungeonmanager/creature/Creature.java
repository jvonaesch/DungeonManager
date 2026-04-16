package dungeonmanager.creature;

import dungeonmanager.feature.Features;
import dungeonmanager.stats.*;
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
        return "\n\"%s\":\n %s%s".formatted(
                name,
                StatSets.toString(stats, 1),
                Features.toString(feature, 1)
        );
    }

    @Override
    public ModifiableStatSet getStatSet() {
        return stats;
    }
}
