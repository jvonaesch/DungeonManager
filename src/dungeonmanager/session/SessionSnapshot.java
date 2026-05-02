package dungeonmanager.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dungeonmanager.contentpack.JsonSerializable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static dungeonmanager.contentpack.PackLoader.writeToFile;

/**
 * Immutable transport payload for session state.
 * <p>
 * This is the canonical JSON form for workspace session persistence.
 */
public class SessionSnapshot implements JsonSerializable {

    public static final int CURRENT_SCHEMA_VERSION = 1;
    public static final String SESSION_FILENAME = "session.json";

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final int schemaVersion;
    private final String selectedCreatureId;
    private final long nextCreatureNumber;
    private final SequencedCollection<CreatureSnapshot> creatures;

    public SessionSnapshot(int schemaVersion, String selectedCreatureId, long nextCreatureNumber, SequencedCollection<CreatureSnapshot> creatures) {
        this.schemaVersion = schemaVersion;
        this.selectedCreatureId = selectedCreatureId;
        this.nextCreatureNumber = nextCreatureNumber;
        this.creatures = creatures != null ? Collections.unmodifiableSequencedCollection(new LinkedList<>(creatures)) : new LinkedList<>();
    }

    public SessionSnapshot(int schemaVersion, String selectedCreatureId, long nextCreatureNumber) {
        this(schemaVersion, selectedCreatureId, nextCreatureNumber, new LinkedList<>());
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public String getSelectedCreatureId() {
        return selectedCreatureId;
    }

    public long getNextCreatureNumber() {
        return nextCreatureNumber;
    }

    public static Path getSessionPath(Path workspacePath) {
        return workspacePath.resolve(SESSION_FILENAME);
    }

    public void save(Path workspacePath) throws IOException {
        writeToFile(getSessionPath(workspacePath), MAPPER.writeValueAsString(toJson()));
    }

    public static Optional<SessionSnapshot> loadIfPresent(Path workspacePath) throws IOException {
        Path sessionFile = getSessionPath(workspacePath);
        if (!Files.exists(sessionFile)) {
            return Optional.empty();
        }
        return Optional.of(fromJson(Files.readString(sessionFile)));
    }

    public static SessionSnapshot load(Path workspacePath) throws IOException {
        Path sessionFile = getSessionPath(workspacePath);
        if (!Files.exists(sessionFile)) {
            throw new IOException("Session snapshot not found: " + sessionFile.toAbsolutePath());
        }
        return fromJson(Files.readString(sessionFile));
    }

    @Override
    public JsonNode toJson() {
        try {
            return MAPPER.readTree(MAPPER.writeValueAsString(PersistedSession.fromSnapshot(this)));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize session snapshot", e);
        }
    }

    public static SessionSnapshot fromJson(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Session JSON cannot be blank");
        }

        try {
            PersistedSession persisted = MAPPER.readValue(json, PersistedSession.class);
            return persisted.toSnapshot();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid session snapshot JSON", e);
        }
    }

    /**
     * @return A list of snapshots of all <b>local</b> creatures <i>(No library-owned ones)</i>
     */
    public SequencedCollection<CreatureSnapshot> getCreatures() {
        return creatures;
    }

    private static final class PersistedSession {
        public int schemaVersion;
        public String selectedCreatureId;
        public long nextCreatureNumber;

        public PersistedSession() {
        }

        private static PersistedSession fromSnapshot(SessionSnapshot snapshot) {
            PersistedSession persisted = new PersistedSession();
            persisted.schemaVersion = snapshot.getSchemaVersion();
            persisted.selectedCreatureId = snapshot.getSelectedCreatureId();
            persisted.nextCreatureNumber = snapshot.getNextCreatureNumber();
            return persisted;
        }

        private SessionSnapshot toSnapshot() throws IOException {
            if (schemaVersion != CURRENT_SCHEMA_VERSION) {
                throw new IOException("Unsupported schema version: " + schemaVersion);
            }
            return new SessionSnapshot(schemaVersion, selectedCreatureId, nextCreatureNumber);
        }
    }
}
