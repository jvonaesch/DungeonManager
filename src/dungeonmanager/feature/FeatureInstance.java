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
        
        loadSections();
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
    
    public void reloadSections() {
        sections.clear();
        loadSections();
    }

    private void loadSections() {
        for (FeatureSection section : feature.getSections()) {
            addSection(section);
        }
    }

    protected void addSection(FeatureSection section) {
        if (section != null) {
            sections.add(section);
            section.onAdd(stat_context);
        }
    }

    protected FeatureSection removeSection(int index) {
        if (index >= 0 && index < sections.size()) {
            FeatureSection removed = sections.remove(index);
            removed.onRemove(stat_context);
            return removed;
        }
        return null;
    }

    public List<FeatureSection> getSections() {
        return new ArrayList<>(sections);
    }

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
