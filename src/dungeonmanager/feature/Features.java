package dungeonmanager.feature;

import dungeonmanager.stats.StatModifier;

/**
 * Utility class for converting features and feature sets to strings for display purposes.
 */
public class Features {

    public static String toString(FeatureSet set, int indent) {
        String space = "\t".repeat(indent);
        StringBuilder string = new StringBuilder("\n%sFeatures:".formatted(space));
        for (FeatureInstance instance: set.getAllFeatures()) {
            string.append(Features.toString(instance, indent + 1));
        }
        return string.toString();
    }

    public static String toString(FeatureSet set) {
        return toString(set, 0);
    }

    public static String toString(FeatureInstance instance, int indent) {
        String space = "\t".repeat(indent);
        StringBuilder string = new StringBuilder("\n%s%s".formatted(space, instance.getName()));
        for (StatModifier modifier: instance.getStatModifiers()) {
            string.append("\n%s>\t\"%s\"".formatted(space, instance.getDescription()));
        }
        
        for (FeatureSection section : instance.getSections()) {
            if (section.isVisible()) {
                string.append("\n%s## %s".formatted(space, section.getName()));
                string.append("\n%s%s".formatted(space, section.getDescription()));
            }
        }
        
        return string.toString();
    }

    public static String toString(FeatureInstance instance) {
        return toString(instance, 0);
    }
}
