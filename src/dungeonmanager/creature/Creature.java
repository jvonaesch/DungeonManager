package dungeonmanager.creature;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dungeonmanager.contentpack.JsonSerializable;
import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.feature.Features;
import dungeonmanager.session.Session;
import dungeonmanager.stat.*;
import dungeonmanager.feature.FeatureSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class Creature implements CreatureBasis, JsonSerializable {

    static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;
    private String name;
    private CreatureBasis type;
    private final DefaultedStatSet statSet;
    private final FeatureSet feature;
    private final StatContext statContext;

    public Creature(@NotNull StatContext statContext, String id, String name, CreatureBasis type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.statSet = new DefaultedStatSet(statContext, new DefaultStatSet(statContext));
        this.feature = new FeatureSet(this.statSet);
        this.statContext = statContext;

        this.changeType(type);
    }

    public Creature(@NotNull StatContext statContext, String id, String name) {
        this(statContext, id, name, null);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    public String toString(Session session) {
        return "\n\"%s\":\n %s%s".formatted(
                name,
                StatSets.toString(statSet, 1, session),
                Features.toString(feature, 1)
        );
    }

    public CreatureBasis getBasis() {
        return type;
    }

    public void changeType(CreatureBasis type) {
        if (type == null) this.statSet.changeParent(new DefaultStatSet(this.statContext));
        else this.statSet.changeParent(type);
        this.type = type;
    }

    public void rename(String name) {
        this.name = name;
    }

    @Override
    public ModifiableStatSet getStatSet() {
        return statSet;
    }

    public FeatureSet getFeatureSet() {
        return feature;
    }

    @Override
    public JsonNode toJson() {
        ObjectNode obj = MAPPER.createObjectNode();
        obj.put("name", name);
        obj.put("typeId", type != null ? type.getId() : null);

        // Serialize base stat overrides
        ObjectNode baseStatsNode = MAPPER.createObjectNode();
        Map<String, Integer> baseValues = statSet.getBaseValues();
        baseValues.forEach(baseStatsNode::put);
        obj.set("baseStatOverrides", baseStatsNode);

        ArrayNode featureNode = MAPPER.createArrayNode();
        Collection<FeatureInstance> features = feature.getAllFeatures();
        for (FeatureInstance feature: features) {
            featureNode.add(feature.toJson());
        }
        // TODO: serialize features

        return obj;
    }

    public static Creature fromJson(String creatureId, JsonNode json, Session session) {
        String name = json.path("name").asText();
        String typeId = json.path("typeId").asText(null);
        StatContext statContext = session.getStatContext();

        // Resolve creature type from session library
        CreatureBasis creatureType = null;
        if (typeId != null && !typeId.isEmpty()) {
            creatureType = session.library.creature.get(typeId);
            if (creatureType == null) {
                throw new IllegalArgumentException("Creature type not found in library: " + typeId);
            }
        }

        Creature creature = new Creature(statContext, creatureId, name, creatureType);

        // Restore base stat overrides
        JsonNode baseStatsNode = json.path("baseStatOverrides");
        if (baseStatsNode.isObject()) {
            baseStatsNode.fields().forEachRemaining(entry -> {
                String statId = entry.getKey();
                int value = entry.getValue().asInt();
                Stat stat = statContext.getStat(statId);
                if (stat != null) {
                    creature.getStatSet().setBaseValue(stat, value);
                }
            });
        }

        // TODO: restore features

        return creature;
    }
}
