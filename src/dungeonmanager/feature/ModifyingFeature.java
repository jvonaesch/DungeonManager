package dungeonmanager.feature;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dungeonmanager.contentpack.JsonSerializable;
import dungeonmanager.session.Session;

import java.util.ArrayList;
import java.util.List;

public class ModifyingFeature implements JsonSerializable {

    static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;
    private String name;
    private String description;
    private List<FeatureSection> sections;

    public ModifyingFeature(String id, String name, String description) {
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

    public ModifyingFeature addSection(FeatureSection section) {
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

    public static ModifyingFeature fromJson(String featureId, JsonNode json, Session session) {
        String featureName = json.path("name").asText();
        String featureDesc = json.path("description").asText();

        ModifyingFeature feature = new ModifyingFeature(featureId, featureName, featureDesc);

        JsonNode sectionsArray = json.path("sections");
        if (!sectionsArray.isArray()) throw new IllegalArgumentException("sections field in json must be an ArrayNode");
        sectionsArray.forEach(element -> {
            FeatureSection deserialized = FeatureSerializer.loadSection(element);
            if (deserialized != null) {
                feature.addSection(deserialized);
            }
        });

        return feature;
    }
}
