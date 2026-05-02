package dungeonmanager.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureSerializer {

    static final ObjectMapper MAPPER = new ObjectMapper();
    static final Logger LOG = LoggerFactory.getLogger(FeatureSerializer.class);

    /**
     * Helper method to deserialize a FeatureSection from JSON based on its type.
     * Supports score_modifiers and selection section types.
     *
     * @param json the JSON string representation of the section
     * @return the deserialized FeatureSection, or null if type is unknown
     */
    public static FeatureSection loadSection(JsonNode json) {
        String type = json.path("type").asText();

        if ("score_modifiers".equals(type)) {
            return StatModifierSection.fromJson(json);
        } else if ("selection".equals(type)) {
            return SelectionSection.fromJson(json);
        }
        return null;
    }

}
