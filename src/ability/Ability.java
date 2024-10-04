package ability;

import java.util.EnumMap;
import java.util.function.Consumer;

public enum Ability {
    STR("STR", "strength"),
    CON("CON", "constitution"),
    DEX("DEX", "dexterity"),
    INT("INT", "intelligence"),
    WIS("WIS", "wisdom"),
    CHA("CHA", "charisma");

    final String id;
    final String name;

    Ability(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static void populateAbilityMap(EnumMap <Ability, Integer> map, int default_value) {
        for (Ability ability : Ability.values()) {
            if (!map.containsKey(ability)) {
                map.put(ability, default_value);
            }
        }
    }
    public static void populateAbilityMap(EnumMap <Ability, Integer> map) {
        populateAbilityMap(map, 0);
    }

    public static void forEach(Consumer<Ability> consumer) {
        for (Ability ability: Ability.values()) {
            consumer.accept(ability);
        }
    }

    public static EnumMap<Ability, AbilityScore> getNewDefaultScores () {
        EnumMap<Ability, AbilityScore> scores = new EnumMap<Ability, AbilityScore> (Ability.class);
        for (Ability ability: Ability.values()) {
            scores.put(ability, new AbilityScore(ability));
        }
        return scores;
    }
}
