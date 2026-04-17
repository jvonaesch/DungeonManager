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

    /**
     * Brings a stat set into a readable format sorted by <i>stat type</i>.
     * <br>
     * relies on the implementation's {@link  StatSet#getSpecifiedStats()} method.
     *
     * @param set stat set for which to get the map <b>M</b>
     * @return map <b>M</b> from <i>stat types</i> (ability, base_stat, ...) to maps <b>M_type</b>,
     * which each map <i>stat IDs</i> of stats with their <i>type</i> to their respective stat <i>value</i>
     * in the set.
     * @see dungeonmanager.stats.StatSet#getSpecifiedStats() for stat discovery
     */
    public static Map<String, Map<Stat, Integer>> getByType(StatSet set) {
        TreeMap<String, Map<Stat, Integer>> map = new TreeMap<>();
        for (Stat stat: set.getSpecifiedStats()) {
            String type = stat.getType();
            if (!map.containsKey(stat.getType())) map.put(stat.getType(), new TreeMap<> (Stats.getDefaultComparator()));
            map.get(stat.getType()).put(stat, set.getValue(stat));
        }
        return map;
    }

    /**
     * Helper method for formatting map entries with indentation.
     * @param map the map to format
     * @param indent indentation level
     * @return formatted string with indented entries
     */
    public static String mapString(Map<?, Integer> map, int indent) {
        String space = "\t".repeat(indent);
        StringBuilder string = new StringBuilder();
        for (Object key: map.keySet()) {
            string.append("\n%s> %s: %d".formatted(space, key, map.get(key)));
        }
        return string.toString();
    }

    /**
     * Formats a stat set as a hierarchical string organized by stat type.
     * @param set the stat set to format
     * @param indent base indentation level
     * @return formatted string with abilities, base stats, and other stat types
     */
    public static String toString(StatSet set, int indent) {
        String space = "\t".repeat(indent);
        StringBuilder string = new StringBuilder();
        Map<String, Map<Stat, Integer>> type_map = getByType(set);
        string.append("%sAbilities:".formatted(space));
        string.append(mapString(type_map.remove("ability"), indent + 1));
        string.append("\n%sBase stats:".formatted(space));
        string.append(mapString(type_map.remove("base_stat"), indent + 1));
        for (String stat_type: type_map.keySet()) {
            string.append("\n%s\"%s\":".formatted(space, stat_type));
            Map<Stat, Integer> sub_map = type_map.get(stat_type);
            string.append(mapString(sub_map, indent+1));
        }
        return string.toString();
    }

    public static String toString(StatSet set) {
        return toString(set, 0);
    }
}
