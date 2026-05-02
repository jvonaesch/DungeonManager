package dungeonmanager.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dungeonmanager.contentpack.JsonSerializable;
import dungeonmanager.session.Session;

import java.util.ArrayList;
import java.util.List;

public class Feature implements JsonSerializable {

    static final ObjectMapper MAPPER = new ObjectMapper();

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
    public JsonNode toJson() {
        ObjectNode json = MAPPER.createObjectNode();
        json.put("name", name);
        json.put("description", description);

        ArrayNode sectionsArray = MAPPER.createArrayNode();
        for (FeatureSection section : sections) {
            JsonNode sectionJson = section.toJson();
            if (sectionJson == null) {
                continue;
            }
            sectionsArray.add(sectionJson);
        }
        json.set("sections", sectionsArray);
        return json;
    }

    public static Feature fromJson(String featureId, JsonNode json, Session session) {
        String featureName = json.path("name").asText();
        String featureDesc = json.path("description").asText();

        Feature feature = new Feature(featureId, featureName, featureDesc);

        JsonNode sectionsArray = json.path("sections");
        if (sectionsArray.isArray()) {
            sectionsArray.forEach(element -> {
                try {
                    String sectionJson = MAPPER.writeValueAsString(element);
                    FeatureSection deserialized = FeatureSerializer.loadSection(sectionJson);
                    if (deserialized != null) {
                        feature.addSection(deserialized);
                    }
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException("Failed to deserialize feature section", e);
                }
            });
        }

        return feature;
    }
}
