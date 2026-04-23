package dungeonmanager.stat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dungeonmanager.session.Session;

import java.util.Iterator;
import java.util.Map;

import static dungeonmanager.contentpack.PackLoader.MAPPER;

public interface WriteableStatSet extends StatSet {

    void setBaseValue(String statId, Integer value);

    default void setBaseValue(IStat stat, Integer value) {
        setBaseValue(stat.getId(), value);
    }

    void resetBaseValue(IStat stat);

    void removeBaseValue(String statId);

    default void removeBaseValue(IStat stat) {
        removeBaseValue(stat.getId());
    }

    @Override
    default StatSet jsonPopulate(String json, Session session) throws JsonProcessingException {
        JsonNode node = MAPPER.readTree(json);
        Iterator<String> it = node.fieldNames();
        for (String statId = it.next(); it.hasNext(); statId = it.next()) {
            this.setBaseValue(statId, node.get(statId).asInt());
        }
        return this;
    }

    @Override
    default String toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        for (Map.Entry<String, Integer> entry: getValues().entrySet()) {
            node.put(entry.getKey(), entry.getValue());
        }
        try {
            return MAPPER.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
