package dungeonmanager.stat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dungeonmanager.session.Session;

import java.util.*;

import static dungeonmanager.contentpack.PackLoader.MAPPER;

/**
 * Singleton class representing the default set of stat for a creature.
 */
public class BaseStatSet implements WriteableStatSet {

    protected Map<String, Integer> values;

    public BaseStatSet() {
        this.values = new TreeMap<>();
    }

    public BaseStatSet(Map<String, Integer> values) {
        if (values == null) {
            this.values = new TreeMap<>();
            for (IStat stat: StandardStat.values()) {
                this.values.put(stat.getId(), stat.getDefaultValue());
            }
        }
        else this.values = new TreeMap<> (values);
    }

    @Override
    public Integer getValue(String statId) {
        return values.get(statId);
    }

    @Override
    public Set<String> getSpecifiedStats() {
        return values.keySet();
    }

    @Override
    public void setBaseValue(String statId, Integer value) {
        values.put(statId, value);
    }

    @Override
    public void resetBaseValue(IStat stat) {
        values.put(stat.getId(), stat.getDefaultValue());
    }

    @Override
    public void removeBaseValue(String statId) {
        values.remove(statId);
    }
}
