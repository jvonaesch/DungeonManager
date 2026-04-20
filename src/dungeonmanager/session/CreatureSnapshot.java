package dungeonmanager.session;

import dungeonmanager.creature.Creature;
import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.stats.Stat;

import java.util.*;

public class CreatureSnapshot {
    private final String id;
    private final String name;
    private final String typeId;
    private final String typeName;
    private final Map<String, Integer> stats;
    private final Map<String, Integer> baseStatOverrides;
    private final List<FeatureSnapshot> features;

    CreatureSnapshot(
            String id,
            String name,
            String typeId,
            String typeName,
            Map<String, Integer> stats,
            Map<String, Integer> baseStatOverrides,
            List<FeatureSnapshot> features)
    {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.typeName = typeName;
        this.stats = Collections.unmodifiableMap(new LinkedHashMap<>(stats));
        this.baseStatOverrides = Collections.unmodifiableMap(new LinkedHashMap<>(baseStatOverrides));
        this.features = Collections.unmodifiableList(new ArrayList<>(features));
    }

    static CreatureSnapshot fromCreature(String id, Creature creature) {
        Map<String, Integer> statValues = new LinkedHashMap<>();
        List<Stat> stats = new ArrayList<>(creature.getStatSet().getSpecifiedStats());
        stats.sort(Comparator.comparing(Stat::getID));
        for (Stat stat : stats) {
            statValues.put(stat.getID(), creature.getStatSet().getValue(stat));
        }

        Map<Stat, Integer> baseValueSnapshot = creature.getStatSet().getBaseValues();
        Map<String, Integer> baseOverrides = new LinkedHashMap<>();
        List<Stat> baseOverrideStats = new ArrayList<>(baseValueSnapshot.keySet());
        baseOverrideStats.sort(Comparator.comparing(Stat::getID));
        for (Stat stat : baseOverrideStats) {
            baseOverrides.put(stat.getID(), baseValueSnapshot.get(stat));
        }

        List<FeatureSnapshot> featureSnapshots = new ArrayList<>();
        for (FeatureInstance instance : creature.getFeatureSet().getAllFeatures()) {
            featureSnapshots.add(FeatureSnapshot.fromInstance(instance));
        }
        featureSnapshots.sort(Comparator.comparing(FeatureSnapshot::getInstanceId));

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

    public List<FeatureSnapshot> getFeatures() {
        return features;
    }

    public FeatureSnapshot getFeature(String featureInstanceId) {
        if (featureInstanceId == null) {
            return null;
        }
        String normalizedId = featureInstanceId.trim();
        if (normalizedId.isEmpty()) {
            return null;
        }
        for (FeatureSnapshot feature : features) {
            if (feature.getInstanceId().equals(normalizedId)) {
                return feature;
            }
        }
        return null;
    }
}
