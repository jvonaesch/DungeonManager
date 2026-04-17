package dungeonmanager.feature;

import dungeonmanager.stats.StatModifier;

import java.util.Collection;

/**
 * An instance of a {@link Feature} specific to a {@link dungeonmanager.creature.Creature}. It stores creature-specific
 * choices made for the feature.
 * It also provides the name and description of the feature shown in the creature's UI summary, as well as stat
 * modifiers that the feature applies to the creature <i>if active</i>.
 */
public class FeatureInstance {

    private final Feature feature;
    public final String ID;
    protected boolean active;

    protected FeatureInstance(String ID, Feature feature) {
        this.feature = feature;
        this.ID = ID;
        this.active = false;
    }

    @Override
    public int hashCode() {
        return this.ID.hashCode();
    }

    public Collection<StatModifier> getStatModifiers() {
        return feature.getStatModifiers();
    }

    public String getName() {
        return feature.getName();
    }

    public String getDescription() {
        return feature.getDescription();
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
}
