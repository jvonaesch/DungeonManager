package dungeonmanager.session;

import dungeonmanager.creature.CreatureType;
import dungeonmanager.feature.Feature;

import java.util.Map;
import java.util.function.Function;

final class SessionValidation {

    private SessionValidation() {
    }

    static String normalizeId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be blank");
        }
        return normalized;
    }

    static String normalizeName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        return normalized;
    }

    static CreatureType requireType(CreatureType type) {
        if (type == null) {
            throw new IllegalArgumentException("Creature type cannot be null");
        }
        return type;
    }

    static Feature requireFeature(Feature feature) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature cannot be null");
        }
        return feature;
    }

    static <T> T requireById(Map<String, T> map, String id, Function<String, RuntimeException> errorFactory) {
        String normalizedId = normalizeId(id);
        T value = map.get(normalizedId);
        if (value == null) {
            throw errorFactory.apply(id);
        }
        return value;
    }
}

