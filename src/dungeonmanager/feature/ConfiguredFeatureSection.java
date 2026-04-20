package dungeonmanager.feature;

import java.util.Map;

public interface ConfiguredFeatureSection <T> extends FeatureSection {

    Class<T> getConfigType();

    default T getConfig(Map<String, ?> map) {
        Object item = map.get(this.getID());
        Class<T> configType = this.getConfigType();
        if (configType.isInstance(item)) {
            return configType.cast(item);
        }
        return null;
    }
}

