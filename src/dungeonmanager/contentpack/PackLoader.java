package dungeonmanager.contentpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import dungeonmanager.feature.Feature;
import dungeonmanager.session.Session;
import dungeonmanager.stat.IStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.apache.commons.io.FilenameUtils.removeExtension;

/**
 * Loads data from Content Packs.
 * <br>Scans each pack folder and recursively loads all JSON files.
 * Layout:
 * <pre><code>
 *  └─ pack/
 *     └─ features/
 * </code></pre>
 * Features are registered into Registries.get().feature for global access.
 */
public class PackLoader {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(PackLoader.class);
    private final Session session;

    public PackLoader(Session session) {
        this.session = session;
    }

    public static void writeToFile(Path path, String content) throws IOException {
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Load all packs in a library directory to the PackLoader's session
     * @param libraryPath the path to the DungeonManagerLibrary directory
     */
    public void loadLibrary(Path libraryPath) {

        if (!checkOrMakeDir(libraryPath)) return;

        try (Stream<Path> packDirs = Files.list(libraryPath)) {
            packDirs.filter(Files::isDirectory)
                    .forEach(this::loadPack);

            // TODO: enable loading from zip files

        } catch (IOException e) {
            LOG.error("Error accessing library directory: {}", libraryPath.toAbsolutePath(), e);
        }
    }

    /**
     * Load a content pack to the PackLoader's session
     * Sequence does not matter since registration is lazy and only reads JSON when accessed
     * @param packDir the path to a pack directory
     */
    public void loadPack(Path packDir) {
        String packName = packDir.getFileName().toString();
        Path featuresDir = packDir.resolve("features");

        Path statsPath = packDir.resolve("stats.json");
        File statsFile = statsPath.toFile();
        if (statsFile.exists() && statsFile.isFile() && statsFile.getName().endsWith(".json")) {
            LOG.debug("Loading stats from pack: {}", packName);
            try {
                String json = Files.readString(statsPath);
                Set<IStat> stats = IStat.fromJson(json, packName);
                stats.forEach(session::registerStat);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (checkDir(featuresDir, "skipping features for this pack")) {
            LOG.debug("Loading features from pack: {}", packDir.getFileName());
            loadRecursive(featuresDir, (Path filePath) -> loadFromFile(filePath,
                    (String featureId, String json) -> session.registerFeature(featureId,
                            () -> Feature.fromJson(featureId, json)), "feature"));
        }
    }

    /**
     * Recursively walk a directory and apply a load consumer to each JSON file found
     * @param dir the directory to walk
     * @param fileConsumer a consumer that takes a Path to a JSON file and loads it into the session
     */
    private void loadRecursive(Path dir, Consumer<Path> fileConsumer) {
        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(fileConsumer);
        } catch (IOException e) {
            LOG.error("Error walking directory: {}", dir.toAbsolutePath(), e);
        }
    }

    public void loadFromFile(Path filePath, BiConsumer<String, String> registrar, String contentType) {
        try {
            String json = Files.readString(filePath);
            String featureId = removeExtension(filePath.getFileName().toString());
            registrar.accept(featureId, json);
            LOG.debug("Loaded {} '{}' from {}", contentType, featureId, filePath);
        } catch (IOException e) {
            LOG.error("Error reading {} file: {}", contentType, filePath.toAbsolutePath(), e);
        } catch (Exception e) {
            LOG.error("Error loading {} from {}: {}", contentType, filePath.toAbsolutePath(), e.getMessage(), e);
        }
    }

    public static boolean checkOrMakeDir(Path filePath, String failureMessage, String notADirectoryMessage) {
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(filePath);
            } catch (IOException e) {
                LOG.error(failureMessage, filePath.toAbsolutePath(), e);
                return false;
            }
        }
        if (!Files.isDirectory(filePath)) {
            LOG.error(notADirectoryMessage, filePath.toAbsolutePath());
            return false;
        }
        return true;
    }

    public static boolean checkOrMakeDir(Path filePath) {
        return checkOrMakeDir(
                filePath,
                "Failed to create directory: {}",
                "Expected a directory but found a file: {}"
        );
    }

    public static boolean checkDir(Path dirPath, String absentActionName) {
        if (!Files.exists(dirPath)) {
            LOG.debug("Directory '{}' not found. {}", dirPath.getFileName(), absentActionName);
            return false;
        }
        if (!Files.isDirectory(dirPath)) {
            LOG.debug("'{}' is not a directory. {}", dirPath.getFileName(), absentActionName);
            return false;
        }
        return true;
    }
}

