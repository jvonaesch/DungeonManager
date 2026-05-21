package dungeonmanager.feature;

import dungeonmanager.stat.StatModifier;
import dungeonmanager.stat.ModifiableStatSet;

import java.util.*;

public class FeatureSet {

    private final Map<String, FeatureInstance> features;
    private final ModifiableStatSet stat_context;

    public FeatureSet(ModifiableStatSet stat_context) {
        features = new TreeMap<>();
        this.stat_context = stat_context;
    }

    public FeatureInstance addFeature(String instanceId, ModifyingFeature feature) {
        if (!features.containsKey(instanceId)) {
            FeatureInstance instance = new FeatureInstance(instanceId, feature, stat_context);
            features.put(instance.id, instance);
            return instance;
        }
        return null;
    }

    public boolean addFeature(FeatureInstance instance) {
        if (!features.containsKey(instance.id)) {
            features.put(instance.id, instance);
            return true;
        }
        return false;
    }

    public FeatureInstance addFeature(ModifyingFeature feature) {
        return addFeature(feature.getId(), feature);
    }

    public void disableFeature(String ID) {
        if (features.containsKey(ID)) {
            features.get(ID).active = false;
        }
    }

    public void enableFeature(String ID) {
        if (features.containsKey(ID)) {
            features.get(ID).active = true;
        }
    }

    public FeatureInstance removeFeature(String ID) {
        if (!features.containsKey(ID)) return null;
        FeatureInstance instance = features.get(ID);
        this.removeFeature(instance);
        return instance;
    }

    public boolean removeFeature(FeatureInstance instance) {
        String ID = instance.id;
        if (!features.containsKey(ID) || features.get(ID) != instance) return false;
        for (StatModifier modifier: instance.getStatModifiers()) {
            stat_context.removeModifier(modifier);
        }
        return features.remove(instance.id, instance);
    }

    public Collection<FeatureInstance> getAllFeatures() {
        return features.values();
    }

    public void reload() {
        for (FeatureInstance instance: features.values()) {
            instance.reload();
        }
    }
}
