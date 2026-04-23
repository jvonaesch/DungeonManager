package dungeonmanager.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FeatureSerializer {

    static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Helper method to deserialize a FeatureSection from JSON based on its type.
     * Supports score_modifiers and selection section types.
     *
     * @param json the JSON string representation of the section
     * @return the deserialized FeatureSection, or null if type is unknown
     */
    public static FeatureSection loadSection(String json) {
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

}
