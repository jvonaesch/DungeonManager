package dungeonmanager.stat;

import com.fasterxml.jackson.core.JsonProcessingException;
import dungeonmanager.session.Session;

import java.util.Set;

public class DefaultStatSet implements StatSet {

    private final SessionStatContext context;

    public DefaultStatSet(SessionStatContext context) {
        this.context = context;
    }

    @Override
    public Integer getValue(String statId) {
        Stat stat = context.getStat(statId);
        if (stat == null) return null;
        return context.getStat(statId).getDefaultValue();
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
    public StatSet jsonPopulate(String json, Session session) throws JsonProcessingException {
        return this;
    }

    @Override
    public String toJson() {
        return "";
    }
}
