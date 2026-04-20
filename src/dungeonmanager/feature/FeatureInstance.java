package dungeonmanager.feature;

import dungeonmanager.stats.StatModifier;
import dungeonmanager.stats.ModifiableStatSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final Map<String, Object> config;
    
    protected FeatureInstance(String ID, Feature feature, ModifiableStatSet stat_context) {
        this.feature = feature;
        this.ID = ID;
        this.active = false;
        this.sections = new ArrayList<>();
        this.stat_context = stat_context;
        this.config = new HashMap<>();
        loadSections();
    }

    @Override
    public int hashCode() {
        return this.ID.hashCode();
    }

    public String getName() {
        return feature.getName();
    }

    public String getDescription() {
        return feature.getDescription();
    }

    /**
     * Gets all stat modifiers applied by this feature instance's sections.
     * Recursively collects modifiers from all sections and their subsections.
     * @return collection of all stat modifiers from this feature's sections
     */
    public Collection<StatModifier> getStatModifiers() {
        Set<StatModifier> modifiers = new HashSet<>();
        collectModifiers(sections, modifiers);
        return modifiers;
    }

    /**
     * Recursively collects stat modifiers from a list of sections.
     * @param sectionsToCheck sections to check for modifiers
     * @param collectedModifiers set to add found modifiers to
     */
    private void collectModifiers(List<FeatureSection> sectionsToCheck, Set<StatModifier> collectedModifiers) {
        for (FeatureSection section : sectionsToCheck) {
            if (section instanceof StatModifierSection) {
                collectedModifiers.add(((StatModifierSection) section).getModifier());
            }
        }
    }

    public void reloadSections() {
        sections.clear();
        loadSections();
    }

    private void loadSections() {
        for (FeatureSection section : feature.getSections()) {
            section.loadToInstance(this);
        }
    }

    protected void addSection(FeatureSection section) {
        if (section != null) {
            sections.add(section);
            section.onAdd(stat_context);
        }
    }

    @Deprecated
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

    /**
     * Stores a selection choice for a Section requiring choices.
     * @param selectionID the ID of the Section this selection corresponds to
     * @param choices set of choices (specified by Section type)
     */
    public void setSelection(String selectionID, Object choices) {
        config.put(selectionID, choices);
    }

    /**
     * Gets the stored choices for a given Section.
     * @param selectionID the ID of the SelectionSection
     * @return the stored choices, or null if not set
     */
    public Object getSelection(String selectionID) {
        return config.get(selectionID);
    }

    @Override
    public String toString() {
        return "feature instance %s (feat: %s)\n\t'%s'\n\t%s\n".formatted(
                this.ID,
                this.feature.ID,
                this.feature.getDescription(),
                this.getStatModifiers()
        );
    }
}
