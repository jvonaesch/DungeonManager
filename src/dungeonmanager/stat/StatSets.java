package dungeonmanager.stat;

import dungeonmanager.session.Session;

import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class for advanced stat set formatting and organization.
 * Groups stat by type and provides hierarchical display formatting.
 * Used for creating readable representations of complex stat sets.
 * 
 * @see dungeonmanager.stat.Stats for basic stat utilities
 * @see dungeonmanager.stat.ModifiableStatSet#toString() for simple formatting
 */
public class StatSets {

    public static Map<String, Map<String, Integer>> getByType(StatSet set, Session session) {
        TreeMap<String, Map<String, Integer>> map = new TreeMap<>();
        for (String statId: set.getSpecifiedStats()) {
            IStat stat = session.getStat(statId);
            if (!map.containsKey(stat.getType())) map.put(stat.getType(), new TreeMap<> ());
            map.get(stat.getType()).put(statId, set.getValue(statId));
        }
        return map;
    }

    public static String mapString(Map<?, Integer> map, int indent) {
        String space = "\t".repeat(indent);
        StringBuilder string = new StringBuilder();
        for (Object key: map.keySet()) {
            string.append("\n%s> %s: %d".formatted(space, key, map.get(key)));
        }
        return string.toString();
    }

    public static String toString(StatSet set, int indent, Session session) {
        String space = "\t".repeat(indent);
        StringBuilder string = new StringBuilder();
        Map<String, Map<String, Integer>> type_map = getByType(set, session);
        string.append("%sAbilities:".formatted(space));
        string.append(mapString(type_map.remove("ability"), indent + 1));
        string.append("\n%sBase stat:".formatted(space));
        string.append(mapString(type_map.remove("base_stat"), indent + 1));
        for (String stat_type: type_map.keySet()) {
            string.append("\n%s%s:".formatted(space, stat_type));
            Map<String, Integer> sub_map = type_map.get(stat_type);
            string.append(mapString(sub_map, indent+1));
        }
        return string.toString();
    }

    public static String toString(StatSet set, Session session) {
        return toString(set, 0, session);
    }
}
