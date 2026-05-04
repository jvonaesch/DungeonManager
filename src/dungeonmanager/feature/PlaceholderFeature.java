package dungeonmanager.feature;

import com.fasterxml.jackson.databind.JsonNode;
import dungeonmanager.session.Session;

public class PlaceholderFeature extends Feature {

    Session session;
    JsonNode instanceJson;
    Feature feature;

    public PlaceholderFeature(String featureId, Session session, JsonNode instanceJson) {
        super(featureId, "placeholder feature %s".formatted(featureId),
                "Feature %s was not found! This is a placeholder to preserve for eventual reloading"
                        .formatted(featureId));
        this.session = session;
        this.instanceJson = instanceJson;
    }

    @Override
    public String getId() {
        if (isResolved()) return feature.getId();
        return super.getId();
    }

    @Override
    public String getName() {
        if (isResolved()) return feature.getName();
        return super.getName();
    }

    @Override
    public String getDescription() {
        if (isResolved()) return feature.getDescription();
        return super.getDescription();
    }

    public JsonNode getInstanceJson() {
        return instanceJson;
    }

    boolean isResolved() {
        if (feature != null || session.getFeature(super.getId()) != null) {
            feature = session.getFeature(super.getId());
            return true;
        }
        return false;
    }

    Feature resolveFeature() {
        if (isResolved()) return feature;
        return null;
    }
}
