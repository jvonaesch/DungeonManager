package dungeonmanager.feature;

import dungeonmanager.stats.StatModifier;
import dungeonmanager.stats.ModifiableStatSet;

import java.util.*;

public class FeatureSet {

    private final Map<String, FeatureInstance> features;
    private final ModifiableStatSet stat_context;

    public FeatureSet(ModifiableStatSet stat_context) {
        features = new TreeMap<>();
        this.stat_context = stat_context;
    }

    public FeatureInstance addFeature(String ID, String name, Feature feature) {
        if (!features.containsKey(ID)) {
            FeatureInstance instance = new FeatureInstance(ID, name, feature);
            features.put(instance.ID, instance);
            for (StatModifier modifier: instance.getStatModifiers()) {
                stat_context.addModifier(modifier);
            }
            return instance;
        }
        return null;
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
        String ID = instance.ID;
        if (!features.containsKey(ID) || features.get(ID) != instance) return false;
        for (StatModifier modifier: instance.getStatModifiers()) {
            stat_context.removeModifier(modifier);
        }
        return features.remove(instance.ID, instance);
    }

    public Collection<FeatureInstance> getAllFeatures() {
        return features.values();
    }
}
