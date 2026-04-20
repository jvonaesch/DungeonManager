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
    private final Set<StatModifier> modifiers = new HashSet<>();
    
    protected FeatureInstance(String ID, Feature feature, ModifiableStatSet stat_context) {
        this.feature = feature;
        this.ID = ID;
        this.active = false;
        this.sections = new ArrayList<>();
        this.stat_context = stat_context;
        this.config = new HashMap<>();
        this.reload();
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

    public String getFeatureId() {
        return feature.ID;
    }

    /**
     * Gets all stat modifiers applied by this feature instance's sections.
     * Recursively collects modifiers from all sections and their subsections.
     * @return collection of all stat modifiers from this feature's sections
     */
    public Collection<StatModifier> getStatModifiers() {
        return new HashSet<>(modifiers);
    }

    public void reload() {
        for (StatModifier modifier: modifiers) {
            stat_context.removeModifier(modifier);
        }
        sections.clear();
        for (FeatureSection section : feature.getSections()) {
            section.loadToInstance(this);
        }
        modifiers.clear();
        for (FeatureSection section : sections) {
            if (section instanceof StatModifierSection) {
                modifiers.add(((StatModifierSection) section).getModifier());
            }
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

    public void setSelection(String selectionID, Object choices) {
        config.put(selectionID, choices);
    }

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
