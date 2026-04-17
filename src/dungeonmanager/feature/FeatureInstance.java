package dungeonmanager.feature;

import dungeonmanager.stats.StatModifier;
import dungeonmanager.stats.ModifiableStatSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private final List<FeatureSection> sections;
    private final ModifiableStatSet stat_context;

    protected FeatureInstance(String ID, Feature feature, ModifiableStatSet stat_context) {
        this.feature = feature;
        this.ID = ID;
        this.active = false;
        this.sections = new ArrayList<>();
        this.stat_context = stat_context;
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

    /**
     * Add a section to this feature instance.
     * The section's onAdd method is called to perform any necessary setup.
     * @param section the section to add
     */
    public void addSection(FeatureSection section) {
        if (section != null) {
            sections.add(section);
            section.onAdd(stat_context);
        }
    }

    /**
     * Remove a section from this feature instance by index.
     * The section's onRemove method is called to perform any necessary cleanup.
     * @param index the index of the section to remove
     * @return the removed section, or null if index is out of bounds
     */
    public FeatureSection removeSection(int index) {
        if (index >= 0 && index < sections.size()) {
            FeatureSection removed = sections.remove(index);
            removed.onRemove(stat_context);
            return removed;
        }
        return null;
    }

    /**
     * Get all sections in this feature instance.
     * @return an unmodifiable list of sections
     */
    public List<FeatureSection> getSections() {
        return new ArrayList<>(sections);
    }

    /**
     * Get the number of sections in this feature instance.
     * @return the section count
     */
    public int getSectionCount() {
        return sections.size();
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
