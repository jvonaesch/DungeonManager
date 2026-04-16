package dungeonmanager.feature;

import dungeonmanager.stats.StatModifier;

import java.util.Collection;


public class FeatureInstance {

    //private final Creature creature;
    private final Feature feature;
    public final String ID;
    private String name;
    protected boolean active;

    protected FeatureInstance(String ID, String name, Feature feature) {
        //this.creature = creature;
        this.feature = feature;
        this.ID = ID;
        this.active = false;
        this.name = name;
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

    public Collection<StatModifier> getStatModifiers() {
        // TODO: choices
        return feature.getStatModifiers();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return feature.getDescription();
    }
}
