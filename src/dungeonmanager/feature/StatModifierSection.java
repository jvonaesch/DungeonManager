package dungeonmanager.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dungeonmanager.stat.ModifiableStatSet;
import dungeonmanager.stat.StatModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A FeatureSection that contains a score modifier
 */
public class StatModifierSection implements FeatureSection {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String id;
    private String name;
    private String description;
    private StatModifier modifier;
    private boolean visible;

    public StatModifierSection(String id, String name, String description, StatModifier modifier) {
        this(id, name, description, modifier, true);
    }

    public StatModifierSection(String id, String name, String description, @NotNull StatModifier modifier, boolean visible) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.modifier = modifier;
        this.visible = visible;
    }

    /*public StatModifierSection(String id, String name, String description) {
        this(id, name, description, null, true);
    }*/

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public String getType() {
        return "score_modifiers";
    }

    public StatModifier getModifier() {
        return modifier;
    }

    public void setModifier(StatModifier modifier) {
        this.modifier = modifier;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void onAdd(ModifiableStatSet statSet) {
        if (modifier != null) {
            statSet.addModifier(modifier);
        }
    }

    @Override
    public void onRemove(ModifiableStatSet statSet) {
        if (modifier != null) {
            statSet.removeModifier(modifier);
        }
    }

    @Override
    public String toString() {
        return "ScoreModifierSection{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", modifier=" + modifier +
                ", visible=" + visible +
                '}';
    }

    @Override
    public JsonNode toJson() {
        ObjectNode json = MAPPER.createObjectNode();
        json.put("type", getType());
        json.put("id", id);
        json.put("name", name);
        json.put("description", description);
        json.put("visible", visible);

        if (modifier != null) {
            ObjectNode modifierNode = MAPPER.createObjectNode();
            modifierNode.put("targetStat", modifier.getTargetStatId());
            modifierNode.put("baseValue", modifier.getBaseValue());
            
            ObjectNode dependenciesNode = MAPPER.createObjectNode();
            for (Map.Entry<String, Float> entry : modifier.getDependencies().entrySet()) {
                dependenciesNode.put(entry.getKey(), entry.getValue());
            }
            modifierNode.set("dependencies", dependenciesNode);
            
            json.set("modifier", modifierNode);
        }
        return json;
    }

    public static StatModifierSection fromJson(JsonNode json) {
        String sectionId = json.path("id").asText();
        String sectionName = json.path("name").asText();
        String sectionDesc = json.path("description").asText();
        boolean sectionVisible = json.path("visible").asBoolean(true);

        StatModifier modifier;
        JsonNode modifierData = json.path("modifier");
        if (modifierData.isObject()) {
            String targetStat = modifierData.path("targetStat").asText();
            if (!targetStat.isEmpty()) {
                modifier = new StatModifier(targetStat);
                modifier.setBaseValue(modifierData.path("baseValue").asInt(0));

                JsonNode dependenciesNode = modifierData.path("dependencies");
                if (dependenciesNode.isObject()) {
                    dependenciesNode.fields().forEachRemaining(entry ->
                            modifier.setDependency(entry.getKey(), (float) entry.getValue().asDouble())
                    );
                }
                return new StatModifierSection(sectionId, sectionName, sectionDesc, modifier, sectionVisible);
            }
            throw new IllegalArgumentException("Stat modifier section JSON must include a target stat");
        }
        throw new IllegalArgumentException("could not parse stat modifier section from JSON");

    }
}
