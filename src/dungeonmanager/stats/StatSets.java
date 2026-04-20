package dungeonmanager.stats;

import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class for advanced stat set formatting and organization.
 * Groups stats by type and provides hierarchical display formatting.
 * Used for creating readable representations of complex stat sets.
 * 
 * @see dungeonmanager.stats.Stats for basic stat utilities
 * @see dungeonmanager.stats.ModifiableStatSet#toString() for simple formatting
 */
public class StatSets {

    public static Map<String, Map<Stat, Integer>> getByType(StatSet set) {
        TreeMap<String, Map<Stat, Integer>> map = new TreeMap<>();
        for (Stat stat: set.getSpecifiedStats()) {
            String type = stat.getType();
            if (!map.containsKey(stat.getType())) map.put(stat.getType(), new TreeMap<> (Stats.getDefaultComparator()));
            map.get(stat.getType()).put(stat, set.getValue(stat));
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

    public static String toString(StatSet set, int indent) {
        String space = "\t".repeat(indent);
        StringBuilder string = new StringBuilder();
        Map<String, Map<Stat, Integer>> type_map = getByType(set);
        string.append("%sAbilities:".formatted(space));
        string.append(mapString(type_map.remove("ability"), indent + 1));
        string.append("\n%sBase stats:".formatted(space));
        string.append(mapString(type_map.remove("base_stat"), indent + 1));
        for (String stat_type: type_map.keySet()) {
            string.append("\n%s%s:".formatted(space, stat_type));
            Map<Stat, Integer> sub_map = type_map.get(stat_type);
            string.append(mapString(sub_map, indent+1));
        }
        return string.toString();
    }

    public static String toString(StatSet set) {
        return toString(set, 0);
    }
}
