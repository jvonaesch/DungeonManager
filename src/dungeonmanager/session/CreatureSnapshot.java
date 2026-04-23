package dungeonmanager.session;

import dungeonmanager.creature.Creature;
import dungeonmanager.feature.FeatureInstance;

import java.util.*;

public class CreatureSnapshot {
    private final String id;
    private final String name;
    private final String typeId;
    private final String typeName;
    private final Map<String, Integer> stats;
    private final Map<String, Integer> baseStatOverrides;
    private final List<FeatureInstanceSnapshot> features;

    CreatureSnapshot(
            String id,
            String name,
            String typeId,
            String typeName,
            Map<String, Integer> stats,
            Map<String, Integer> baseStatOverrides,
            List<FeatureInstanceSnapshot> features)
    {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.typeName = typeName;
        this.stats = Collections.unmodifiableMap(new LinkedHashMap<>(stats));
        this.baseStatOverrides = Collections.unmodifiableMap(new LinkedHashMap<>(baseStatOverrides));
        this.features = List.copyOf(features);
    }

    static CreatureSnapshot fromCreature(String id, Creature creature) {
        Map<String, Integer> statValues = new LinkedHashMap<>();
        List<String> statIds = new ArrayList<>(creature.getStatSet().getSpecifiedStats());
        statIds.sort(String::compareTo);
        for (String statId : statIds) {
            statValues.put(statId, creature.getStatSet().getValue(statId));
        }

        Map<String, Integer> baseValueSnapshot = creature.getStatSet().getBaseValues();
        Map<String, Integer> baseOverrides = new LinkedHashMap<>();
        List<String> baseOverrideStats = new ArrayList<>(baseValueSnapshot.keySet());
        baseOverrideStats.sort(String::compareTo);
        for (String statId : baseOverrideStats) {
            baseOverrides.put(statId, baseValueSnapshot.get(statId));
        }

        List<FeatureInstanceSnapshot> featureSnapshots = new ArrayList<>();
        for (FeatureInstance instance : creature.getFeatureSet().getAllFeatures()) {
            featureSnapshots.add(FeatureInstanceSnapshot.fromInstance(instance));
        }
        featureSnapshots.sort(Comparator.comparing(FeatureInstanceSnapshot::getInstanceId));

        return new CreatureSnapshot(
                id,
                creature.getName(),
                creature.getType().getID(),
                creature.getType().getName(),
                statValues,
                baseOverrides,
                featureSnapshots
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public Map<String, Integer> getBaseStatOverrides() {
        return baseStatOverrides;
    }

    public int getStat(String statId) {
        Integer value = stats.get(statId);
        if (value == null) {
            throw new IllegalArgumentException("Stat not present in snapshot: " + statId);
        }
        return value;
    }

    public List<FeatureInstanceSnapshot> getFeatures() {
        return features;
    }

    public FeatureInstanceSnapshot getFeature(String featureInstanceId) {
        if (featureInstanceId == null) {
            return null;
        }
        String normalizedId = featureInstanceId.trim();
        if (normalizedId.isEmpty()) {
            return null;
        }
        for (FeatureInstanceSnapshot feature : features) {
            if (feature.getInstanceId().equals(normalizedId)) {
                return feature;
            }
        }
        return null;
    }
}
