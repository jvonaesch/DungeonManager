package dungeonmanager.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class SessionJsonSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(SessionJsonSerializer.class);

    private static final String LIBRARY_DIR = System.getProperty("user.home") + "\\DungeonManagerLibrary";
    private static final Path SESSIONS_DIR = Paths.get(LIBRARY_DIR, "sessions");
    private static final Path FEATURES_DIR = SESSIONS_DIR.resolve("features");

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static void ensureDirectoriesExist() throws IOException {
        Files.createDirectories(FEATURES_DIR);
    }

    private static Path getFeaturePath(String featureInstanceId) {
        String safeId = encodeId(Session.normalizeId(featureInstanceId));
        return FEATURES_DIR.resolve(safeId + ".json");
    }

    private static String normalizeSessionFilename(String filename) {
        String normalized = Session.normalizeId(filename).replace("\\", "_").replace("/", "_");
        if (!normalized.endsWith(".json")) {
            normalized += ".json";
        }
        return normalized;
    }

    private static String encodeId(String rawId) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawId.getBytes(StandardCharsets.UTF_8));
    }

    private static final class SessionFile {
        private final int schemaVersion;
        private final String selectedCreatureId;
        private final long nextCreatureNumber;
        private final List<PersistedCreature> creatures;
        private final Map<String, FeatureFile> featuresById;

        private SessionFile(
                int schemaVersion,
                String selectedCreatureId,
                long nextCreatureNumber,
                List<PersistedCreature> creatures,
                Map<String, FeatureFile> featuresById
        ) {
            this.schemaVersion = schemaVersion;
            this.selectedCreatureId = selectedCreatureId;
            this.nextCreatureNumber = nextCreatureNumber;
            this.creatures = creatures;
            this.featuresById = featuresById;
        }

        private static SessionFile fromSnapshot(SessionSnapshot snapshot) {
            List<PersistedCreature> creatureFiles = new ArrayList<>();
            Map<String, FeatureFile> features = new LinkedHashMap<>();

            for (CreatureSnapshot creature : snapshot.getCreatures()) {
                List<String> featureInstanceIds = new ArrayList<>();
                for (FeatureInstanceSnapshot feature : creature.getFeatures()) {
                    featureInstanceIds.add(feature.getInstanceId());
                    features.put(feature.getInstanceId(), FeatureFile.fromSnapshot(feature));
                }

                creatureFiles.add(new PersistedCreature(
                        creature.getId(),
                        creature.getName(),
                        creature.getSourceId(),
                        creature.getSourceName(),
                        creature.getStats(),
                        creature.getBaseStatOverrides(),
                        featureInstanceIds
                ));
            }

            return new SessionFile(
                    snapshot.getSchemaVersion(),
                    snapshot.getSelectedCreatureId(),
                    snapshot.getNextCreatureNumber(),
                    creatureFiles,
                    features
            );
        }

        private PersistedSession toPersistedSession() {
            return new PersistedSession(schemaVersion, selectedCreatureId, nextCreatureNumber, creatures);
        }

        private static SessionFile fromPersistedSession(PersistedSession persisted) throws IOException {
            if (persisted == null) {
                throw new IOException("Session JSON was empty");
            }
            if (persisted.schemaVersion != SessionSnapshot.CURRENT_SCHEMA_VERSION) {
                throw new IOException("Unsupported schema version: " + persisted.schemaVersion);
            }

            List<PersistedCreature> creatureFiles = persisted.creatures == null ? List.of() : persisted.creatures;
            Map<String, FeatureFile> features = new LinkedHashMap<>();

            for (PersistedCreature creature : creatureFiles) {
                if (creature.featureInstanceIds == null) {
                    continue;
                }
                for (String instanceId : creature.featureInstanceIds) {
                    FeatureFile feature = readFeatureFile(instanceId);
                    if (!feature.instanceId.equals(instanceId)) {
                        throw new IOException("Feature JSON ID mismatch for " + instanceId);
                    }
                    features.put(instanceId, feature);
                }
            }

            return new SessionFile(
                    persisted.schemaVersion,
                    persisted.selectedCreatureId,
                    persisted.nextCreatureNumber,
                    creatureFiles,
                    features
            );
        }

        private SessionSnapshot toSnapshot() throws IOException {
            List<CreatureSnapshot> creatureSnapshots = new ArrayList<>();
            for (PersistedCreature creature : creatures) {
                List<FeatureInstanceSnapshot> featureSnapshots = new ArrayList<>();
                if (creature.featureInstanceIds != null) {
                    for (String instanceId : creature.featureInstanceIds) {
                        FeatureFile featureFile = featuresById.get(instanceId);
                        if (featureFile == null) {
                            throw new IOException("Missing feature JSON for feature instance: " + instanceId);
                        }
                        featureSnapshots.add(featureFile.toSnapshot());
                    }
                }

                creatureSnapshots.add(new CreatureSnapshot(
                        creature.id,
                        creature.name,
                        creature.typeId,
                        creature.typeName,
                        creature.stats == null ? Map.of() : creature.stats,
                        creature.baseStatOverrides == null ? Map.of() : creature.baseStatOverrides,
                        featureSnapshots
                ));
            }

            return new SessionSnapshot(schemaVersion, selectedCreatureId, creatureSnapshots, nextCreatureNumber);
        }
    }

    private static FeatureFile readFeatureFile(String instanceId) throws IOException {
        Path path = getFeaturePath(instanceId);
        if (!Files.exists(path)) {
            throw new IOException("Feature file not found for feature instance '" + instanceId + "': " + path);
        }
        return MAPPER.readValue(path.toFile(), FeatureFile.class);
    }

    private static final class PersistedSession {
        public int schemaVersion;
        public String selectedCreatureId;
        public long nextCreatureNumber;
        public List<PersistedCreature> creatures;

        public PersistedSession() {
        }

        private PersistedSession(int schemaVersion, String selectedCreatureId, long nextCreatureNumber, List<PersistedCreature> creatures) {
            this.schemaVersion = schemaVersion;
            this.selectedCreatureId = selectedCreatureId;
            this.nextCreatureNumber = nextCreatureNumber;
            this.creatures = creatures;
        }
    }

    private static final class PersistedCreature {
        public String id;
        public String name;
        public String typeId;
        public String typeName;
        public Map<String, Integer> stats;
        public Map<String, Integer> baseStatOverrides;
        public List<String> featureInstanceIds;

        public PersistedCreature() {
        }

        private PersistedCreature(
                String id,
                String name,
                String typeId,
                String typeName,
                Map<String, Integer> stats,
                Map<String, Integer> baseStatOverrides,
                List<String> featureInstanceIds
        ) {
            this.id = id;
            this.name = name;
            this.typeId = typeId;
            this.typeName = typeName;
            this.stats = new LinkedHashMap<>(stats);
            this.baseStatOverrides = new LinkedHashMap<>(baseStatOverrides);
            this.featureInstanceIds = new ArrayList<>(featureInstanceIds);
        }
    }

    private static final class FeatureFile {
        public String instanceId;
        public String featureId;
        public String name;
        public String description;
        public int sectionCount;
        public Map<String, Integer> statModifiers;
        public Map<String, List<String>> config;

        public FeatureFile() {
        }

        private FeatureFile(
                String instanceId,
                String featureId,
                String name,
                String description,
                int sectionCount,
                Map<String, Integer> statModifiers,
                Map<String, List<String>> config
        ) {
            this.instanceId = instanceId;
            this.featureId = featureId;
            this.name = name;
            this.description = description;
            this.sectionCount = sectionCount;
            this.statModifiers = new LinkedHashMap<>(statModifiers);
            this.config = new LinkedHashMap<>(config);
        }

        private static FeatureFile fromSnapshot(FeatureInstanceSnapshot snapshot) {
            return new FeatureFile(
                    snapshot.getInstanceId(),
                    snapshot.getFeatureId(),
                    snapshot.getName(),
                    snapshot.getDescription(),
                    snapshot.getSectionCount(),
                    snapshot.getStatModifiers(),
                    snapshot.getConfig()
            );
        }

        private FeatureInstanceSnapshot toSnapshot() {
            return new FeatureInstanceSnapshot(
                    instanceId,
                    featureId,
                    name,
                    description,
                    sectionCount,
                    statModifiers == null ? Map.of() : statModifiers,
                    config == null ? Map.of() : config
            );
        }
    }
}

