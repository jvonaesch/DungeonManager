package dungeonmanager.creature;

import dungeonmanager.feature.Features;
import dungeonmanager.stats.*;
import dungeonmanager.feature.FeatureSet;

public class Creature implements HasStatSet {

    private String name;
    private CreatureType type;
    public final DefaultedStatSet stats;
    public final FeatureSet feature;

    public Creature(String name, CreatureType type) {
        this.name = name;
        this.type = type;
        this.stats = new DefaultedStatSet(this.type);
        this.feature = new FeatureSet(this.stats);
    }

    public Creature(String name) {
        this(name, IntegratedCreatureType.DEFAULT);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "\n\"%s\":\n %s%s".formatted(
                name,
                StatSets.toString(stats, 1),
                Features.toString(feature, 1)
        );
    }

    public CreatureType getType() {
        return type;
    }

    public void changeType(CreatureType type) {
        this.type = type;
        this.stats.changeParent(type);
    }

    public void rename(String name) {
        this.name = name;
    }

    @Override
    public ModifiableStatSet getStatSet() {
        return stats;
    }
}
