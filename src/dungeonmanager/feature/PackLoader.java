package dungeonmanager.feature;

import dungeonmanager.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Loads feature templates from pack directories.
 * Scans each top-level pack folder for a 'features' subdirectory and recursively loads all JSON files.
 * Features are registered into Registries.get().feature for global access.
 */
public class PackLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PackLoader.class);

    /**
     * Load all features from packs in the library directory.
     * Each pack is expected to have a 'features' folder with JSON files defining features.
     *
     * @param libraryPath the path to the DungeonManagerLibrary directory
     */
    public static void loadFeaturesFromLibrary(String libraryPath) {
        Path libDir = Paths.get(libraryPath);

        if (!Files.exists(libDir)) {
            LOG.warn("Library directory does not exist: {}", libDir.toAbsolutePath());
            return;
        }

        if (!Files.isDirectory(libDir)) {
            LOG.warn("Library path is not a directory: {}", libDir.toAbsolutePath());
            return;
        }

        try (Stream<Path> packDirs = Files.list(libDir)) {
            packDirs.filter(Files::isDirectory)
                    .forEach(PackLoader::loadFeaturesFromPack);
        } catch (IOException e) {
            LOG.error("Error accessing library directory: {}", libDir.toAbsolutePath(), e);
        }
    }

    /**
     * Load features from a single pack root directory.
     * The pack root is expected to contain a 'features' directory.
     *
     * @param packPath the path to a single pack directory
     */
    public static void loadFromPack(String packPath) {
        Path packDir = Paths.get(packPath);
        if (!Files.exists(packDir)) {
            LOG.warn("Pack directory does not exist: {}", packDir.toAbsolutePath());
            return;
        }

        if (!Files.isDirectory(packDir)) {
            LOG.warn("Pack path is not a directory: {}", packDir.toAbsolutePath());
            return;
        }

        loadFeaturesFromPack(packDir);
    }

    /**
     * Load all features from a single pack's 'features' subdirectory.
     *
     * @param packDir the path to a pack directory
     */
    private static void loadFeaturesFromPack(Path packDir) {
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
    private static void loadFeaturesRecursive(Path dir) {
        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(PackLoader::loadFeatureFromFile);
        } catch (IOException e) {
            LOG.error("Error walking directory: {}", dir.toAbsolutePath(), e);
        }
    }

    /**
     * Load a single feature from a JSON file and register it.
     *
     * @param filePath the path to the JSON file
     */
    private static void loadFeatureFromFile(Path filePath) {
        try {
            String json = Files.readString(filePath);
            Feature feature = Feature.fromJson(json);

            if (feature != null) {
                Registries.get().feature.register(feature.ID, feature);
                LOG.debug("Loaded feature '{}' from {}", feature.ID, filePath.getFileName());
            } else {
                LOG.warn("Failed to deserialize feature from {}", filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            LOG.error("Error reading feature file: {}", filePath.toAbsolutePath(), e);
        } catch (Exception e) {
            LOG.error("Error loading feature from {}: {}", filePath.toAbsolutePath(), e.getMessage(), e);
        }
    }
}

