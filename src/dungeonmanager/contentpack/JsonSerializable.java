package dungeonmanager.contentpack;

import com.fasterxml.jackson.databind.JsonNode;
import dungeonmanager.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

import static dungeonmanager.contentpack.PackLoader.MAPPER;
import static dungeonmanager.contentpack.PackLoader.writeToFile;

public interface JsonSerializable{

    static final Logger LOG = LoggerFactory.getLogger(JsonSerializable.class);

    static boolean storeSerializable(JsonSerializable serializable, Path path) {
        if (!org.apache.commons.io.FilenameUtils.isExtension(path.toString(), "json")) {
            LOG.warn("Target file for storeFeature has to be of type json {}", path);
            return false;
        }
        try {
            writeToFile(path, MAPPER.writeValueAsString(serializable.toJson()));
            // writeToFile(path, serializable.toJson().asText());
        } catch (IOException e) {
            LOG.warn("Couldn't write serializable {} to {}", serializable, path);
        }
        return true;
    }

    default boolean storeTo(Path path) {
        return JsonSerializable.storeSerializable(this, path);
    }

    JsonNode toJson();

    static <T> T fromJson(String id, JsonNode json, Session session) {
        throw new UnsupportedOperationException("fromJson() must be implemented by the concrete class");
    };
}
