package dungeonmanager.feature;

import dungeonmanager.creature.Creature;

public class FeatureInstance {
    private final Creature creature;
    private final Feature feature;

    public FeatureInstance(Creature creature, Feature feature) {
        this.creature = creature;
        this.feature = feature;
    }
}
