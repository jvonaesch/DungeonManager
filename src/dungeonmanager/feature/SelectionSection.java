package dungeonmanager.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

/**
 * A FeatureSection that allows choosing from multiple subsections.
 * A specified number of subsections can be selected and added to the FeatureInstance.
 */
public class SelectionSection implements
        ConfiguredFeatureSection<Set<String>>
{

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String id;
    private String name;
    private String description;
    private boolean visible;
    private int numSelections;
    private Map<String, FeatureSection> configuration;

    public SelectionSection(String id, String name, String description, int numSelections) {
        this(id, name, description, numSelections, true);
    }

    public SelectionSection(String id, String name, String description, int numSelections, boolean visible) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.numSelections = numSelections;
        this.visible = visible;
        this.configuration = new HashMap<>();
    }

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
        return "selection";
    }

    /**
     * Adds a choice option to this selection section.
     * @param subsection the subsection to add as a choice
     * @return this SelectionSection for chaining
     */
    public SelectionSection addOption(FeatureSection subsection) {
        if (subsection != null) {
            configuration.put(subsection.getID(), subsection);
        }
        return this;
    }

    /**
     * @return the number of selections required from this section
     */
    public int getNumSelections() {
        return numSelections;
    }

    /**
     * @return map of choice section ID to subsection
     */
    public Map<String, FeatureSection> getConfiguration() {
        return new HashMap<>(configuration);
    }

    /**
     * Generates a compound ID from this section's ID and a choice ID.
     * @param choiceID the selected choice
     * @return compound ID suitable for tracking the selection
     */
    public String getOptionID(String choiceID) {
        return id + ":" + choiceID;
    }

    @Override
    public String toJson() {
        ObjectNode obj = MAPPER.createObjectNode();
        obj.put("type", getType());
        obj.put("id", id);
        obj.put("name", name);
        obj.put("description", description);
        obj.put("visible", visible);
        obj.put("numSelections", numSelections);

        ArrayNode optionsArray = MAPPER.createArrayNode();
        for (Map.Entry<String, FeatureSection> entry : configuration.entrySet()) {
            ObjectNode optionObj = MAPPER.createObjectNode();
            optionObj.put("id", entry.getKey());
            String sectionJson = entry.getValue().toJson();
            if (sectionJson == null) {
                continue;
            }
            try {
                optionObj.set("section", MAPPER.readTree(sectionJson));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to serialize selection option '" + entry.getKey() + "'", e);
            }

            optionsArray.add(optionObj);
        }
        obj.set("options", optionsArray);

        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize selection section '" + id + "'", e);
        }
    }

    public static SelectionSection fromJson(String json) {
        JsonNode obj;
        try {
            obj = MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid selection section JSON", e);
        }

        String sectionId = obj.path("id").asText();
        String sectionName = obj.path("name").asText();
        String sectionDesc = obj.path("description").asText();
        boolean sectionVisible = obj.path("visible").asBoolean(true);
        int numSel = obj.path("numSelections").asInt();

        SelectionSection section = new SelectionSection(sectionId, sectionName, sectionDesc, numSel, sectionVisible);

        JsonNode optionsArray = obj.path("options");
        if (optionsArray.isArray()) {
            optionsArray.forEach(element -> {
                String optionId = element.path("id").asText();
                JsonNode sectionObj = element.path("section");
                String sectionJson;
                try {
                    sectionJson = MAPPER.writeValueAsString(sectionObj);
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException("Failed to deserialize selection option", e);
                }

                // Deserialize based on type
                FeatureSection deserialized = FeatureSerializer.loadSection(sectionJson);
                if (deserialized != null) {
                    section.configuration.put(optionId, deserialized);
                }
            });
        }

        return section;
    }

    @Override
    public void loadToInstance(FeatureInstance instance) {
        instance.addSection(this);
        Object selection_object = instance.getSelection(this.id);
        Set<String> selection;
        if (selection_object instanceof Set) selection = (Set<String>) selection_object;
        else {
            selection = new TreeSet<>();
            instance.setConfig(this.id, selection);
        }

        for (String key : selection) {
            configuration.get(key).loadToInstance(instance);
        }
    }

    @Override
    public Class getConfigType() {
        return SelectionConfig.class;
    }

    public static class SelectionConfig extends TreeSet<String> {}
}
