package dungeonmanager.stat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

import static dungeonmanager.contentpack.PackLoader.MAPPER;


public interface Stat {

    static String toJson(Set<Stat> stats) {
        ArrayNode root = MAPPER.createArrayNode();
        root.addAll(stats.stream().map(stat -> {
            ObjectNode statNode = MAPPER.createObjectNode();
            statNode.put("id", stat.getId());
            statNode.put("name", stat.getName());
            statNode.put("type", stat.getType());
            statNode.put("default_value", stat.getDefaultValue());
            return statNode;
        }).toList());
        try {
            return MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize CustomStat set to JSON", e);
        }
    }

    static Set<Stat> fromJson(String json, String originIdIn) {
        Set<Stat> stats = new HashSet<>();
        try {
            JsonNode node = MAPPER.readTree(json);
            if (!node.isArray()) {
                throw new IllegalArgumentException("Expected JSON array for CustomStat, got: " + json);
            }
            for (JsonNode statNode : node) {
                String id = statNode.path("id").asText();
                String name = statNode.path("name").asText();
                String type = statNode.path("type").asText("base_stat");
                int defaultValue = statNode.path("default_value").asInt(0);
                String originIdentifier = originIdIn + ":" + type + ':' + id;
                DynamicStat stat = new DynamicStat(id, name, type, defaultValue, originIdentifier);
                stats.add(stat);
            }
            return stats;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for CustomStat: " + json, e);
        }
    }

    String getName();
    String getId();

    @SuppressWarnings("unused")
    String getOriginIdentifier();

    String getType();

    int getDefaultValue();
}
