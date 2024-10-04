package ability;

import java.util.EnumMap;

public enum Ability {
    STR("STR"),
    CON("CON"),
    DEX("DEX"),
    INT("INT"),
    WIS("WIS"),
    CHA("CHA");

    final String name;
    Ability(String name_in) {
        name = name_in;
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
}
