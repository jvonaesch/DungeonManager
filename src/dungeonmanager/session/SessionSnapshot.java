package dungeonmanager.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SessionSnapshot {
    public static final int CURRENT_SCHEMA_VERSION = 1;

    private final int schemaVersion;
    private final String selectedCreatureId;
    private final List<CreatureSnapshot> creatures;
    private final long nextCreatureNumber;

    SessionSnapshot(int schemaVersion, String selectedCreatureId, List<CreatureSnapshot> creatures, long nextCreatureNumber) {
        this.schemaVersion = schemaVersion;
        this.selectedCreatureId = selectedCreatureId;
        this.creatures = Collections.unmodifiableList(new ArrayList<>(creatures));
        this.nextCreatureNumber = nextCreatureNumber;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public String getSelectedCreatureId() {
        return selectedCreatureId;
    }

    public List<CreatureSnapshot> getCreatures() {
        return creatures;
    }

    public long getNextCreatureNumber() {
        return nextCreatureNumber;
    }

    public int getCreatureCount() {
        return creatures.size();
    }

    public CreatureSnapshot getCreature(String creatureId) {
        String normalizedId = creatureId == null ? null : creatureId.trim();
        if (normalizedId == null || normalizedId.isEmpty()) {
            return null;
        }
        for (CreatureSnapshot creature : creatures) {
            if (creature.getId().equals(normalizedId)) {
                return creature;
            }
        }
        return null;
    }
}
