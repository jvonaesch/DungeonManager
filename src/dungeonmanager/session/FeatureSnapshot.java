package dungeonmanager.session;

import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StatModifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class FeatureSnapshot {
    private final String instanceId;
    private final String featureId;
    private final String name;
    private final String description;
    private final int sectionCount;
    private final Map<String, Integer> statModifiers;

    FeatureSnapshot(
            String instanceId,
            String featureId,
            String name,
            String description,
            int sectionCount,
            Map<String, Integer> statModifiers
    ) {
        this.instanceId = instanceId;
        this.featureId = featureId;
        this.name = name;
        this.description = description;
        this.sectionCount = sectionCount;
        this.statModifiers = Collections.unmodifiableMap(new LinkedHashMap<>(statModifiers));
    }

    static FeatureSnapshot fromInstance(FeatureInstance instance) {
        Map<String, Integer> modifierTotals = new LinkedHashMap<>();
        for (StatModifier modifier : instance.getStatModifiers()) {
            for (Map.Entry<Stat, Integer> entry : modifier.getValues().entrySet()) {
                String statId = entry.getKey().getID();
                modifierTotals.put(statId, modifierTotals.getOrDefault(statId, 0) + entry.getValue());
            }
        }
        return new FeatureSnapshot(
                instance.ID,
                instance.getFeatureId(),
                instance.getName(),
                instance.getDescription(),
                instance.getSectionCount(),
                modifierTotals
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

    public int getModifier(String statId) {
        Integer value = statModifiers.get(statId);
        return value == null ? 0 : value;
    }
}
