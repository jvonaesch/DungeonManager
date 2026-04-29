package dungeonmanager.session;

import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.stat.StatModifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FeatureInstanceSnapshot {
    private final String instanceId;
    private final String featureId;
    private final String name;
    private final String description;
    private final int sectionCount;
    private final Map<String, Integer> statModifiers;
    private final Map<String, List<String>> config;

    FeatureInstanceSnapshot(
            String instanceId,
            String featureId,
            String name,
            String description,
            int sectionCount,
            Map<String, Integer> statModifiers,
            Map<String, List<String>> selectionConfig
    ) {
        this.instanceId = instanceId;
        this.featureId = featureId;
        this.name = name;
        this.description = description;
        this.sectionCount = sectionCount;
        this.statModifiers = Collections.unmodifiableMap(new LinkedHashMap<>(statModifiers));

        Map<String, List<String>> copiedSelectionConfig = new TreeMap<>();
        for (Map.Entry<String, List<String>> entry : selectionConfig.entrySet()) {
            copiedSelectionConfig.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        this.config = Collections.unmodifiableMap(copiedSelectionConfig);
    }

    static FeatureInstanceSnapshot fromInstance(FeatureInstance instance) {
        Map<String, Integer> modifierTotals = new LinkedHashMap<>();
        for (StatModifier modifier : instance.getStatModifiers()) {
            String statId = modifier.getTargetStatId();
            modifierTotals.put(statId, modifierTotals.getOrDefault(statId, 0) + modifier.getBaseValue());
        }
        return new FeatureInstanceSnapshot(
                instance.ID,
                instance.getFeatureId(),
                instance.getName(),
                instance.getDescription(),
                instance.getSectionCount(),
                modifierTotals,
                instance.getConfigSnapshot()
        );
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getFeatureId() {
        return featureId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public Map<String, Integer> getStatModifiers() {
        return statModifiers;
    }

    public Map<String, List<String>> getConfig() {
        return config;
    }

    public List<String> getConfigFor(String selectionId) {
        List<String> choices = config.get(selectionId);
        return choices == null ? List.of() : choices;
    }

    public int getModifier(String statId) {
        Integer value = statModifiers.get(statId);
        return value == null ? 0 : value;
    }
}

