package dungeonmanager.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dungeonmanager.session.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

public class Feature implements JsonSerializable<Feature> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Can be used as a _prerequisite_ reference for other features
     */
    public final String ID;
    private String name;
    private String description;
    private List<FeatureSection> sections;
    //public final LinkedList<String> prerequisites;

    public Feature(String id, String name, String description) {
        this.ID = id;
        this.description = description;
        this.name = name;
        this.sections = new ArrayList<>();
        //this.prerequisites = new LinkedList<>(prerequisites);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return this.name;
    }

    public Feature addSection(FeatureSection section) {
        if (section != null) {
            sections.add(section);
        }
        return this;
    }

    public void removeSection(FeatureSection section) {
        sections.remove(section);
    }

    public List<FeatureSection> getSections() {
        return new ArrayList<>(sections);
    }

    public int getSectionCount() {
        return sections.size();
    }

    @Override
    public String toJson() {
        ObjectNode obj = MAPPER.createObjectNode();
        obj.put("id", ID);
        obj.put("name", name);
        obj.put("description", description);

        ArrayNode sectionsArray = MAPPER.createArrayNode();
        for (FeatureSection section : sections) {
            String sectionJson = serializeSection(section);
            if (sectionJson == null) {
                continue;
            }
            try {
                sectionsArray.add(MAPPER.readTree(sectionJson));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to serialize section '" + section.getID() + "'", e);
            }
        }
        obj.set("sections", sectionsArray);

        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize feature '" + ID + "'", e);
        }
    }

    public static Feature fromJson(String json) {
        JsonNode obj;
        try {
            obj = MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid feature JSON", e);
        }

        String featureId = obj.path("id").asText();
        String featureName = obj.path("name").asText();
        String featureDesc = obj.path("description").asText();

        Feature feature = new Feature(featureId, featureName, featureDesc);

        JsonNode sectionsArray = obj.path("sections");
        if (sectionsArray.isArray()) {
            sectionsArray.forEach(element -> {
                String sectionJson;
                try {
                    sectionJson = MAPPER.writeValueAsString(element);
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException("Failed to deserialize feature section", e);
                }
                FeatureSection deserialized = deserializeSection(sectionJson);
                if (deserialized != null) {
                    feature.addSection(deserialized);
                }
            });
        }

        return feature;
    }

    /**
     * Helper method to deserialize a FeatureSection from JSON based on its type.
     * Supports score_modifiers and selection section types.
     *
     * @param json the JSON string representation of the section
     * @return the deserialized FeatureSection, or null if type is unknown
     */
    private static FeatureSection deserializeSection(String json) {
        JsonNode obj;
        try {
            obj = MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid section JSON", e);
        }

        String type = obj.path("type").asText();

        if ("score_modifiers".equals(type)) {
            return StatModifierSection.fromJson(json);
        } else if ("selection".equals(type)) {
            return SelectionSection.fromJson(json);
        }
        return null;
    }

    private static String serializeSection(FeatureSection section) {
        if (section instanceof StatModifierSection statModifierSection) {
            return statModifierSection.toJson();
        }
        if (section instanceof SelectionSection selectionSection) {
            return selectionSection.toJson();
        }
        return null;
    }
}
