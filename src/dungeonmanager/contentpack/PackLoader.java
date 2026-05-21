package dungeonmanager.contentpack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dungeonmanager.creature.Creature;
import dungeonmanager.feature.ModifyingFeature;
import dungeonmanager.session.Session;
import dungeonmanager.stat.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
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
 *     ├─ creatures/
 *     ├─ features/
 *     └─ stats.json
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
     * Load all packs in a library directory to the PackLoader's handle
     * @param libraryPath the path to the DungeonManagerLibrary directory
     */
    public void loadPacks(Path libraryPath) {

        if (!checkOrMakeDir(libraryPath)) return;
        LOG.debug("Loading content packs from library: {}", libraryPath.toAbsolutePath());

        try (Stream<Path> packDirs = Files.list(libraryPath)) {
            packDirs.filter(Files::isDirectory)
                    .forEach(this::loadPack);

            // TODO: enable loading from zip files

        } catch (IOException e) {
            LOG.error("Error accessing library directory: {}", libraryPath.toAbsolutePath(), e);
        }
    }

    /**
     * Load a content pack to the PackLoader's handle
     * Sequence does not matter since registration is lazy and only reads JSON when accessed
     * @param packDir the path to a pack directory
     */
    public void loadTo(Path packDir,
                       BiConsumer<String, JsonNode> creatureRegistrar,
                       BiConsumer<String, JsonNode> featureRegistrar,
                       Consumer<Stat> statRegistrar) {
        String packName = packDir.getFileName().toString();
        Path featuresDir = packDir.resolve("features");
        Path creaturesDir = packDir.resolve("creatures");

        Path statsPath = packDir.resolve("stats.json");
        File statsFile = statsPath.toFile();
        if (statsFile.exists() && statsFile.isFile() && statsFile.getName().endsWith(".json")) {
            LOG.debug("Loading stats from pack: {}", packName);
            try {
                String json = Files.readString(statsPath);
                Set<Stat> stats = Stat.fromJson(json, packName);
                stats.forEach(statRegistrar);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (checkDir(featuresDir, "skipping features for this pack")) {
            LOG.debug("Loading features from pack: {}", packDir.getFileName());
            loadRecursive(featuresDir, (Path filePath) -> loadFromFile(filePath,
                    featureRegistrar, "feature"));
        }

        if (checkDir(creaturesDir, "skipping creatures for this pack")) {
            LOG.debug("Loading creatures from pack: {}", packDir.getFileName());
            loadRecursive(creaturesDir, (Path filePath) -> loadFromFile(filePath,
                    creatureRegistrar, "creature"));
        }
    }

    public void loadPack(Path packPath) {
        LOG.debug("Loading content pack from {}", packPath.toAbsolutePath());
        this.loadTo(
                packPath,
                (String creatureId, JsonNode json) -> session.library.creature.putLocked(creatureId,
                        () -> Creature.fromJson(creatureId, json, session)),
                (String featureId, JsonNode json) -> session.library.feature.putLocked(featureId,
                        () -> ModifyingFeature.fromJson(featureId, json, session)),
                session::registerStat);
    }

    public void loadWorkspace(Path workspacePath) {
        LOG.debug("Loading content from workspace {}", workspacePath.toAbsolutePath());
        this.loadTo(
                workspacePath,
                (String creatureId, JsonNode json) -> session.library.creature.putOwned(creatureId,
                        () -> Creature.fromJson(creatureId, json, session)),
                (String featureId, JsonNode json) -> session.library.feature.putOwned(featureId,
                        () -> ModifyingFeature.fromJson(featureId, json, session)),
                session::addStat);
    }

    /**
     * Recursively walk a directory and apply a load consumer to each JSON file found
     * @param dir the directory to walk
     * @param fileConsumer a consumer that takes a Path to a JSON file and loads it into the handle
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

    public void loadFromFile(Path filePath, BiConsumer<String, JsonNode> registrar, String contentType) {
        try {
            String json_text = Files.readString(filePath);
            JsonNode json = MAPPER.readTree(json_text);
            String featureId = removeExtension(filePath.getFileName().toString());
            registrar.accept(featureId, json);
            LOG.debug("Loaded {} '{}' from {}", contentType, featureId, filePath);
        } catch (IOException e) {
            LOG.error("Error reading {} file: {}", contentType, filePath.toAbsolutePath(), e);
        } catch (Exception e) {
            LOG.error("Error loading {} from {}: {}", contentType, filePath.toAbsolutePath(), e.getMessage(), e);
        }
    }

    public static void saveCreaturesToPack(Path packDir, Map<String, Creature> creatures) throws IOException {
        Path creaturesDir = packDir.resolve("creatures");
        checkOrMakeDir(creaturesDir);

        for (Map.Entry<String, Creature> entry : creatures.entrySet()) {
            String creatureId = entry.getKey();
            Creature creature = entry.getValue();
            Path creaturePath = creaturesDir.resolve(creatureId + ".json");
            writeToFile(creaturePath, MAPPER.writeValueAsString(creature.toJson()));
            LOG.debug("Saved creature '{}' to {}", creatureId, creaturePath);
        }
    }

    /**
     * Save feature templates into the pack under the "features" directory.
     */
    public static void saveFeaturesToPack(Path packDir, Map<String, ModifyingFeature> features) throws IOException {
        Path featuresDir = packDir.resolve("features");
        checkOrMakeDir(featuresDir);

        for (Map.Entry<String, ModifyingFeature> entry : features.entrySet()) {
            String featureId = entry.getKey();
            ModifyingFeature feature = entry.getValue();
            Path featurePath = featuresDir.resolve(featureId + ".json");
            writeToFile(featurePath, MAPPER.writeValueAsString(feature.toJson()));
            LOG.debug("Saved feature '{}' to {}", featureId, featurePath);
        }
    }

    /**
     * Save stat definitions into the pack as a top-level stats.json file.
     */
    public static void saveStatsToPack(Path packDir, Map<String, Stat> stats) throws IOException {
        Path statsPath = packDir.resolve("stats.json");
        // Convert map values to a Set for Stat.toJson
        java.util.Set<Stat> statSet = new java.util.HashSet<>(stats.values());
        writeToFile(statsPath, Stat.toJson(statSet));
        LOG.debug("Saved {} stats to {}", statSet.size(), statsPath);
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

