package dungeonmanager.stat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dungeonmanager.session.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static dungeonmanager.contentpack.PackLoader.MAPPER;

public class DefaultStatSet implements StatSet {

    private final StatContext context;

    public DefaultStatSet(@NotNull StatContext context) {
        this.context = context;
    }

    @Override
    public int getValue(String statId) {
        Stat stat = context.getStat(statId);
        return stat == null ? 0 : context.getStat(statId).getDefaultValue();
    }

    @Override
    public Set<String> getSpecifiedStats() {
        return context.getAllIDs();
    }

    @Override
    public boolean hasStat(String statId) {
        return context.getStat(statId) != null;
    }

    @Override
    public JsonNode toJson() {
       return MAPPER.createObjectNode();
    }

    public static DefaultStatSet fromJson(String id, JsonNode json, Session session) {
        return new DefaultStatSet(session.getStatContext());
    }
}
