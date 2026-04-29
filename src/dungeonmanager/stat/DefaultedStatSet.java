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

    private HasStatSet parent;
    private StatSet parentSet;
    private final Set<String> removed;

    public DefaultedStatSet(HasStatSet parent) {
        this(parent.getStatSet());
        this.parent = parent;
    }

    public DefaultedStatSet(StatSet parentSet) {
        super();
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
    public Integer getBaseValue(String statId) {
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
        Map<String, Integer> values = new HashMap<>(parentSet.getValues());
        values.putAll(baseValues);
        for (String statId : removed) {
            values.remove(statId);
        }
        return Collections.unmodifiableMap(values);
    }

    public void changeParent(HasStatSet newParent) {
        this.parent = newParent;
        this.parentSet = newParent.getStatSet();
        this.dirtyStats.addAll(getSpecifiedStats());
    }

    @Deprecated
    public void changeParent(StatSet newParentSet) {
        this.parent = null;
        this.parentSet = newParentSet;
        this.dirtyStats.addAll(getSpecifiedStats());
    }

    @Override
    public StatSet jsonPopulate(String json, Session session) throws JsonProcessingException {
        // TODO: populate from parent set
        JsonNode node = MAPPER.readTree(json);
        this.changeParent(session.getCreature(node.get("parent").asText()));
        JsonNode values = node.get("values");
        super.jsonPopulate(MAPPER.writeValueAsString(values), session);
        return this;
    }

    @Override
    public String toJson() {
        // TODO: implement json serialization
        return "";
    }
}
