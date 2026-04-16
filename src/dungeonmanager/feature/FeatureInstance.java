package dungeonmanager.feature;

import dungeonmanager.ability.AbilityModifier;
import dungeonmanager.creature.Creature;

import java.util.Collection;


public class FeatureInstance {

    //private final Creature creature;
    private final Feature feature;
    public String ID;
    protected boolean active;

    protected FeatureInstance(String ID, Feature feature) {
        //this.creature = creature;
        this.feature = feature;
        this.ID = ID;
        this.active = false;
    }

    @Override
    public int hashCode() {
        return this.ID.hashCode();
    }

    @Override
    public String toString() {
        return "feature instance %s (feat: %s)\n\t'%s'\n\t%s\n".formatted(
                this.ID,
                this.feature.ID,
                this.feature.getDescription(),
                this.feature.getStatModifiers()
        );
    }

    public Collection<AbilityModifier> getStatModifiers() {
        // TODO: choices
        return feature.getStatModifiers();
    }
}
