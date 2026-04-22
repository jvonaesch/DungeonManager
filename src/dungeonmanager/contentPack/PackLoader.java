package dungeonmanager.contentPack;

import dungeonmanager.feature.Feature;
import dungeonmanager.registry.Registries;
import dungeonmanager.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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

    private static final Logger LOG = LoggerFactory.getLogger(PackLoader.class);
    private final Session session;

    public PackLoader(Session session) {
        this.session = session;
    }

    /**
     * Load all packs in a library directory
     * @param libraryPath the path to the DungeonManagerLibrary directory
     */
    public void loadLibrary(String libraryPath) {
        Path libDir = Paths.get(libraryPath);

        if (!checkDirectory(libDir)) return;

        try (Stream<Path> packDirs = Files.list(libDir)) {
            packDirs.filter(Files::isDirectory)
                    .forEach(this::loadPack);

            // TODO: enable loading from zip files

        } catch (IOException e) {
            LOG.error("Error accessing library directory: {}", libDir.toAbsolutePath(), e);
        }
    }

    public void loadFromPack(String packPath) {
        Path packDir = Paths.get(packPath);
        if (!Files.exists(packDir)) {
            LOG.warn("Pack directory does not exist: {}", packDir.toAbsolutePath());
            return;
        }

        if (!Files.isDirectory(packDir)) {
            LOG.warn("Pack path is not a directory: {}", packDir.toAbsolutePath());
            return;
        }

        loadPack(packDir);
    }

    /**
     * Load all features from a single pack's 'features' subdirectory.
     *
     * @param packDir the path to a pack directory
     */
    private void loadPack(Path packDir) {
        Path featuresDir = packDir.resolve("features");

        if (!Files.exists(featuresDir)) {
            LOG.debug("Pack '{}' has no features directory, skipping", packDir.getFileName());
            return;
        }

        if (!Files.isDirectory(featuresDir)) {
            LOG.debug("Features path in pack '{}' is not a directory, skipping", packDir.getFileName());
            return;
        }

        LOG.debug("Loading features from pack: {}", packDir.getFileName());
        loadFeaturesRecursive(featuresDir);
    }

    /**
     * Recursively load all JSON files from a directory and its subdirectories.
     *
     * @param dir the directory to scan
     */
    private void loadFeaturesRecursive(Path dir) {
        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(this::loadFeatureFromFile);
        } catch (IOException e) {
            LOG.error("Error walking directory: {}", dir.toAbsolutePath(), e);
        }
    }

    /**
     * Load a single feature from a JSON file and register it.
     *
     * @param filePath the path to the JSON file
     */
    private void loadFeatureFromFile(Path filePath) {
        try {
            String json = Files.readString(filePath);
            Feature feature = Feature.fromJson(json);
            session.registerFeature(feature);
            LOG.debug("Loaded feature '{}' from {}", feature.getId(), filePath.getFileName());
        } catch (IOException e) {
            LOG.error("Error reading feature file: {}", filePath.toAbsolutePath(), e);
        } catch (Exception e) {
            LOG.error("Error loading feature from {}: {}", filePath.toAbsolutePath(), e.getMessage(), e);
        }
    }

    public static boolean checkDirectory(Path filePath, String failureMessage, String notADirectoryMessage) {
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

    public static boolean checkDirectory(Path filePath) {
        return checkDirectory(
                filePath,
                "Failed to create directory: {}",
                "Expected a directory but found a file: {}"
        );
    }
}

