package dungeonmanager.stat;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    public StatSet jsonPopulate(String json, Session session) {
        return this;
    }

    @Override
    public String toJson() {
        try {
            return MAPPER.writeValueAsString(context.getDefaults());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize default stat set", e);
        }
    }
}
