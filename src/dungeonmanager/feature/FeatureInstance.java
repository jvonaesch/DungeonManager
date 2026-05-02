package dungeonmanager.feature;

import com.fasterxml.jackson.databind.JsonNode;
import dungeonmanager.contentpack.JsonSerializable;
import dungeonmanager.session.Session;
import dungeonmanager.stat.StatModifier;
import dungeonmanager.stat.ModifiableStatSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static dungeonmanager.contentpack.PackLoader.MAPPER;

/**
 * An instance of a {@link Feature} specific to a {@link dungeonmanager.creature.Creature}. It stores creature-specific
 * choices made for the feature.
 * It also provides the name and description of the feature shown in the creature's UI summary, as well as stat
 * modifiers that the feature applies to the creature <i>if active</i>.
 */
public class FeatureInstance implements JsonSerializable {

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
        return feature.getId();
    }

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

    public void setConfig(String selectionID, Object choices) {
        config.put(selectionID, choices);
    }

    public Object getSelection(String selectionID) {
        return config.get(selectionID);
    }

    /**
     * Exports selection-style config in a deterministic shape for snapshot persistence.
     */
    public Map<String, List<String>> getConfigSnapshot() {
        Map<String, List<String>> snapshot = new TreeMap<>();
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            if (!(entry.getValue() instanceof Set<?> rawChoices)) {
                continue;
            }
            Set<String> normalizedChoices = new TreeSet<>();
            for (Object value : rawChoices) {
                if (value != null) {
                    normalizedChoices.add(value.toString());
                }
            }
            snapshot.put(entry.getKey(), List.copyOf(normalizedChoices));
        }
        return snapshot;
    }

    /**
     * Applies a saved configuration snapshot to this feature instance.
     * Used when rehydrating from a snapshot to restore user selections.
     * @param configSnapshot map of config IDs to lists of selected option IDs
     */
    public void loadConfigSnapshot(Map<String, List<String>> configSnapshot) {
        for (Map.Entry<String, List<String>> entry : configSnapshot.entrySet()) {
            String configID = entry.getKey();
            List<String> config = entry.getValue();
            Set<String> configSet = new TreeSet<>(config);
            setConfig(configID, configSet);
        }
    }

    @Override
    public String toString() {
        return "feature instance %s (feat: %s)\n\t'%s'\n\t%s\n".formatted(
                this.ID,
                this.feature.getId(),
                this.feature.getDescription(),
                this.getStatModifiers()
        );
    }

    @Override
    public JsonNode toJson() {
        return MAPPER.createObjectNode();
        // TODO: feature instance serialization
    }

    public static FeatureInstance fromJson(String instanceId, JsonNode json, Session session) {
        return null;
        // TODO: feature instance deserialization
    }
}
