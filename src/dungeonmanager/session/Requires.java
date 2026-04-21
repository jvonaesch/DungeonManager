package dungeonmanager.session;

import dungeonmanager.creature.CreatureType;

import java.util.Map;
import java.util.function.Function;

class Requires {
    static CreatureType requireType(CreatureType type) {
        if (type == null) {
            throw new IllegalArgumentException("Creature type cannot be null");
        }
        return type;
    }

    static <T> T requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("%s cannot be null".formatted(name));
        }
        return value;
    }

    static <T> T requireNonNull(T value) {
        return requireNonNull(value, "Value");
    }

    static <T> T requireById(Map<String, T> map, String id, Function<String, RuntimeException> errorFactory) {
        String normalizedId = Session.normalizeId(id);
        T value = map.get(normalizedId);
        if (value == null) {
            throw errorFactory.apply(id);
        }
        return value;
    }
}

