package dungeonmanager.contentpack;

import com.fasterxml.jackson.core.JsonProcessingException;
import dungeonmanager.session.Session;

public interface JsonLoadable <T> extends JsonSerializable {
    T jsonPopulate(String json, Session session) throws JsonProcessingException;
}
