package dungeonmanager.session;

import dungeonmanager.creature.Creature;
import dungeonmanager.creature.CreatureType;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.FeatureInstance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Session owner for the current party.
 *
 * The session keeps live creature objects private and exposes immutable snapshots
 * for UI and future persistence layers.
 */
public class Session {

    private final Map<String, CreatureEntry> creatures;
    private String selectedCreatureId;
    private long nextCreatureNumber;

    public Session() {
        this.creatures = new LinkedHashMap<>();
        this.nextCreatureNumber = 1L;
    }

    public synchronized CreatureSnapshot createCreature(String name) {
        return createCreature(name, IntegratedCreatureType.DEFAULT);
    }

    public synchronized CreatureSnapshot createCreature(String name, CreatureType type) {
        return createCreature(name, type, null);
    }

    public synchronized CreatureSnapshot createCreature(String name, CreatureType type, Map<String, Integer> baseStats) {
        String creatureId = nextCreatureId();
        Creature creature = new Creature(SessionValidation.normalizeName(name), SessionValidation.requireType(type));
        CreatureEntry entry = new CreatureEntry(creatureId, creature);
        creatures.put(creatureId, entry);
        selectedCreatureId = creatureId;

        if (baseStats != null) {
            applyBaseStats(creature, baseStats);
        }

        return snapshotCreature(creatureId);
    }

    public synchronized boolean selectCreature(String creatureId) {
        String normalizedId = SessionValidation.normalizeId(creatureId);
        if (!creatures.containsKey(normalizedId)) {
            return false;
        }
        selectedCreatureId = normalizedId;
        return true;
    }

    public synchronized void clearSelection() {
        selectedCreatureId = null;
    }

    public synchronized String getSelectedCreatureId() {
        return selectedCreatureId;
    }

    public synchronized CreatureSnapshot getSelectedCreatureSnapshot() {
        if (selectedCreatureId == null) {
            return null;
        }
        return snapshotCreature(selectedCreatureId);
    }

    public synchronized CreatureSnapshot getCreatureSnapshot(String creatureId) {
        return snapshotCreature(creatureId);
    }

    public synchronized boolean hasCreature(String creatureId) {
        return creatures.containsKey(SessionValidation.normalizeId(creatureId));
    }

    public synchronized CreatureSnapshot renameCreature(String creatureId, String name) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.rename(SessionValidation.normalizeName(name));
        return snapshotCreature(entry.id);
    }

    public synchronized CreatureSnapshot changeCreatureType(String creatureId, CreatureType type) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.changeType(SessionValidation.requireType(type));
        return snapshotCreature(entry.id);
    }

    public synchronized boolean deleteCreature(String creatureId) {
        String normalizedId = SessionValidation.normalizeId(creatureId);
        CreatureEntry removed = creatures.remove(normalizedId);
        if (removed == null) {
            return false;
        }
        if (Objects.equals(selectedCreatureId, normalizedId)) {
            selectedCreatureId = creatures.isEmpty() ? null : creatures.keySet().iterator().next();
        }
        return true;
    }

    public synchronized CreatureSnapshot setBaseStat(String creatureId, String statId, Integer value) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.getStatSet().setBaseValue(SessionValidation.normalizeId(statId), value);
        return snapshotCreature(entry.id);
    }

    public synchronized CreatureSnapshot resetBaseStat(String creatureId, String statId) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.getStatSet().resetBaseValue(SessionValidation.normalizeId(statId));
        return snapshotCreature(entry.id);
    }

    public synchronized CreatureSnapshot removeBaseStat(String creatureId, String statId) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.getStatSet().removeBaseValue(SessionValidation.normalizeId(statId));
        return snapshotCreature(entry.id);
    }

    public synchronized CreatureSnapshot addFeature(String creatureId, Feature feature) {
        FeatureInstance added = addFeatureInstance(creatureId, feature == null ? null : feature.ID, feature);
        return added == null ? null : snapshotCreature(SessionValidation.normalizeId(creatureId));
    }

    public synchronized CreatureSnapshot addFeature(String creatureId, String featureInstanceId, Feature feature) {
        FeatureInstance added = addFeatureInstance(creatureId, featureInstanceId, feature);
        return added == null ? null : snapshotCreature(SessionValidation.normalizeId(creatureId));
    }

    public synchronized CreatureSnapshot removeFeature(String creatureId, String featureInstanceId) {
        CreatureEntry entry = requireCreature(creatureId);
        FeatureInstance removed = entry.creature.feature.removeFeature(SessionValidation.normalizeId(featureInstanceId));
        return removed == null ? null : snapshotCreature(entry.id);
    }

    public synchronized SessionSnapshot snapshot() {
        List<CreatureSnapshot> creatureSnapshots = new ArrayList<>();
        for (CreatureEntry entry : creatures.values()) {
            creatureSnapshots.add(toCreatureSnapshot(entry));
        }
        return new SessionSnapshot(selectedCreatureId, creatureSnapshots, nextCreatureNumber);
    }

    private FeatureInstance addFeatureInstance(String creatureId, String featureInstanceId, Feature feature) {
        CreatureEntry entry = requireCreature(creatureId);
        Feature requiredFeature = SessionValidation.requireFeature(feature);
        String normalizedFeatureId = SessionValidation.normalizeId(featureInstanceId == null ? requiredFeature.ID : featureInstanceId);
        return entry.creature.feature.addFeature(normalizedFeatureId, requiredFeature);
    }

    private CreatureSnapshot snapshotCreature(String creatureId) {
        CreatureEntry entry = requireCreature(creatureId);
        return toCreatureSnapshot(entry);
    }

    private CreatureSnapshot toCreatureSnapshot(CreatureEntry entry) {
        return CreatureSnapshot.fromCreature(entry.id, entry.creature);
    }

    private void applyBaseStats(Creature creature, Map<String, Integer> baseStats) {
        for (Map.Entry<String, Integer> entry : baseStats.entrySet()) {
            creature.getStatSet().setBaseValue(SessionValidation.normalizeId(entry.getKey()), entry.getValue());
        }
    }

    private CreatureEntry requireCreature(String creatureId) {
        return SessionValidation.requireById(
                creatures,
                creatureId,
                id -> new IllegalArgumentException("Creature not found: " + id)
        );
    }

    private String nextCreatureId() {
        return "creature-" + nextCreatureNumber++;
    }

    private static final class CreatureEntry {
        private final String id;
        private final Creature creature;

        private CreatureEntry(String id, Creature creature) {
            this.id = id;
            this.creature = creature;
        }
    }

}

