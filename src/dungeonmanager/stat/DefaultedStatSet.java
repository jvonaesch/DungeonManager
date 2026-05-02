package dungeonmanager.stat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dungeonmanager.session.Session;

import java.util.*;

import static dungeonmanager.contentpack.PackLoader.MAPPER;

/**
 * StatSet implementation that inherits values from a parent stat set.
 * Supports stat removal and reset operations, falling back to parent values when needed.
 */
public class DefaultedStatSet extends ModifiableStatSet {

    private StatSet parentSet;
    private final Set<String> removed;

    public DefaultedStatSet(StatContext statContext, HasStatSet parent) {
        this(statContext, parent.getStatSet());
    }

    public DefaultedStatSet(StatContext statContext, StatSet parentSet) {
        super(statContext);
        this.parentSet = parentSet;
        this.removed = new HashSet<>();
    }

    /**
     * Sets the base value for a stat, <b>marking it as not removed</b>.
     *
     * @param statId the stat to modify
     * @param value  the new base value, or null to remove
     */
    @Override
    public void setBaseValue(String statId, Integer value) {
        super.setBaseValue(statId, value);
        this.removed.remove(statId);
    }

    /**
     * Removes the base value for a stat and marks it as removed.
     * Removed stat will not fall back to parent values.
     *
     * @param statId the stat to remove
     */
    @Override
    public void removeBaseValue(String statId) {
        super.removeBaseValue(statId);
        removed.add(statId);
    }

    @Override
    public void resetBaseValue(Stat stat) {
        super.resetBaseValue(stat);
        this.removed.remove(stat.getId());
    }

    @Override
    public int getBaseValue(String statId) {
        if (baseValues.containsKey(statId)) return baseValues.get(statId);
        return parentSet.getValue(statId);
    }

    @Override
    public Set<String> getSpecifiedStats() {
        Set<String> specified = super.getSpecifiedStats();
        specified.addAll(parentSet.getSpecifiedStats());
        specified.removeAll(removed);
        return specified;
    }

    @Override
    public Map<String, Integer> getBaseValues() {
        Map<String, Integer> values = new HashMap<>(baseValues);
        // values.putAll(parentSet.getValues());
        for (String statId : removed) {
            values.remove(statId);
        }
        return Collections.unmodifiableMap(values);
    }

    public void changeParent(HasStatSet newParent) {
        this.parentSet = newParent.getStatSet();
        this.dirtyStats.addAll(getSpecifiedStats());
    }

    public void changeParent(StatSet newParentSet) {
        this.parentSet = newParentSet;
        this.dirtyStats.addAll(getSpecifiedStats());
    }

    @Override
    public StatSet jsonPopulate(String json, Session session) throws JsonProcessingException {
        JsonNode node = MAPPER.readTree(json);
        if (node == null || !node.isObject()) {
            throw new IllegalArgumentException("Expected JSON object for defaulted stat set");
        }

        baseValues.clear();
        removed.clear();

        for (Iterator<String> it = node.fieldNames(); it.hasNext(); ) {
            String statId = it.next();
            baseValues.put(statId, node.get(statId).asInt());
        }
        return this;
    }

    @Override
    public JsonNode toJson() {
        try {
            return MAPPER.readTree(MAPPER.writeValueAsString(baseValues));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize defaulted stat set", e);
        }
    }

    public static DefaultedStatSet fromJson(String id, JsonNode json, Session session) {
        if (!json.isObject()) throw new RuntimeException("ModifiableStatSet json must be an ObjectNode");
        StatContext context = session.getStatContext();
        DefaultedStatSet statSet = new DefaultedStatSet(context, new DefaultStatSet(context));
        json.fieldNames().forEachRemaining((String statId) -> statSet.setBaseValue(statId, json.get(statId).intValue()));
        return statSet;
    }
}
