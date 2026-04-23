package dungeonmanager.contentpack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

import static dungeonmanager.contentpack.PackLoader.writeToFile;

public interface JsonSerializable{

    static final Logger LOG = LoggerFactory.getLogger(JsonSerializable.class);

    static boolean storeSerializable(JsonSerializable serializable, Path path) {
        if (!path.endsWith(".json")) {
            LOG.warn("Target file for storeFeature has to be of type json");
            return false;
        }
        try {
            writeToFile(path, serializable.toJson());
        } catch (IOException e) {
            LOG.warn("Couldn't write serializable {} to {}", serializable, path);
        }
        return true;
    }

    default boolean storeTo(Path path) {
        return JsonSerializable.storeSerializable(this, path);
    }

    String toJson();

    // Move to another interface and make non-static, only to be used for Feature Sections and other mutable elements
    static <T> T fromJson(String json) {
        throw new UnsupportedOperationException("fromJson() must be implemented by the concrete class");
    };
}
