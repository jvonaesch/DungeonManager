package dungeonmanager.contentpack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dungeonmanager.session.Session;

public interface JsonLoadable <T> extends JsonSerializable {
    T jsonPopulate(JsonNode json, Session session) throws JsonProcessingException;
}
