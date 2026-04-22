package dungeonmanager.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dungeonmanager.session.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

public class Feature implements JsonSerializable {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;
    private String name;
    private String description;
    private List<FeatureSection> sections;

    public Feature(String id, String name, String description) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.sections = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return description;
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
        obj.put("id", id);
        obj.put("name", name);
        obj.put("description", description);

        ArrayNode sectionsArray = MAPPER.createArrayNode();
        for (FeatureSection section : sections) {
            String sectionJson = section.toJson(); //serializeSection(section);
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
            throw new IllegalStateException("Failed to serialize feature '" + id + "'", e);
        }
    }

    /**
     * Deserialize a Feature from JSON.
     * @param json the JSON string representation of the feature
     * @return the deserialized Feature, or null if deserialization fails
     */
    public static Feature fromJson(String json) {
        try {
            JsonNode obj = MAPPER.readTree(json);
            String featureId = obj.path("id").asText();
            String featureName = obj.path("name").asText();
            String featureDesc = obj.path("description").asText();

            Feature feature = new Feature(featureId, featureName, featureDesc);

            JsonNode sectionsArray = obj.path("sections");
            if (sectionsArray.isArray()) {
                sectionsArray.forEach(element -> {
                    try {
                        String sectionJson = MAPPER.writeValueAsString(element);
                        FeatureSection deserialized = Sections.loadSection(sectionJson);
                        if (deserialized != null) {
                            feature.addSection(deserialized);
                        }
                    } catch (JsonProcessingException e) {
                        throw new IllegalStateException("Failed to deserialize feature section", e);
                    }
                });
            }

            return feature;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid feature JSON", e);
        }
    }
}
