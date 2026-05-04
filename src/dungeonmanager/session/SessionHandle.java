package dungeonmanager.session;

import dungeonmanager.creature.Creature;
import dungeonmanager.creature.CreatureBasis;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.library.SessionLibrary;
import dungeonmanager.stat.DynamicStat;
import dungeonmanager.stat.Stat;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static dungeonmanager.session.Session.normalizeId;
import static dungeonmanager.session.Session.normalizeName;

/**
 * Since the handle gives direct access to its creatures/features, migrate synchronized front-end access
 * to this class.
 */
public class SessionHandle {

    private static final Logger LOG = LoggerFactory.getLogger(SessionHandle.class);
    private final Session session;
    private final SessionLibrary library;

    /**
     * The ID of the currently selected creature. This is null if no creature is selected.
     * <br><b>Note:</b> <i>Always</i> handle null values!
     */
    private String selectedCreatureId;
    private long nextCreatureNumber;

    public SessionHandle(Session session) {
        this.session = session;
        this.library = session.library;
        this.nextCreatureNumber = 1L;
        loadSnapshot();
    }

    public synchronized CreatureSnapshot createCreature(String name) {
        return createCreature(name, "default", null);
    }

    public synchronized CreatureSnapshot createCreature(String name, String basisId) {
        return createCreature(name, basisId, null);
    }

    public synchronized CreatureSnapshot createCreature(String name, @NotNull CreatureBasis type, Map<String, Integer> baseStats) {
        String creatureId = normalizeName(name).toLowerCase().replaceAll("\\s+", "_");
        if (library.creature.containsKey(creatureId)) {
            creatureId += "_" + nextCreatureNumber++;
        }

        Creature creature = new Creature(
                session.getStatContext(),
                creatureId,
                normalizeName(name),
                type);
        if (baseStats != null) applyBaseStats(creature, baseStats);

        library.creature.putOwned(creatureId, creature);
        selectedCreatureId = creatureId;

        LOG.info("Created creature {} named '{}' locally as type {}", creatureId, creature.getName(), creature.getBasis().getId());
        return snapshotCreature(creatureId);
    }

    public CreatureSnapshot createCreature(String name, String basisId, Map<String, Integer> baseStats) {
        return createCreature(name, resolveCreatureType(basisId), baseStats);
    }

    public CreatureSnapshot createCreature(String name, Map<String, Integer> baseStats) {
        return createCreature(name, "default", baseStats);
    }

    public synchronized Map<String, Integer> getStatDefaults() {
        return session.getStatContext().getDefaults();
    }

    public synchronized boolean selectCreature(String creatureId) {
        creatureId = normalizeId(creatureId);
        if (!library.creature.containsKey(creatureId)) {
            LOG.debug("Selection ignored for unknown creature {}", creatureId);
            return false;
        }
        selectedCreatureId = creatureId;
        LOG.debug("Selected creature {}", creatureId);
        return true;
    }

    public synchronized void clearSelection() {
        selectedCreatureId = null;
        LOG.debug("Cleared creature selection");
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
        return library.creature.containsKey(normalizeId(creatureId));
    }

    public synchronized CreatureSnapshot renameCreature(String creatureId, String name) {
        Creature creature = requireCreature(creatureId);
        creature.rename(normalizeName(name));
        LOG.debug("Renamed creature {} to '{}'", creature.getId(), creature.getName());
        return snapshotCreature(creature.getId());
    }

    public synchronized CreatureSnapshot changeCreatureType(String creatureId, CreatureBasis type) {
        Creature creature = requireCreature(creatureId);
        creature.changeType(Requires.requireNonNull(type, "Creature type"));
        LOG.debug("Changed creature {} type to {}", creature.getId(), creature.getBasis().getId());
        return snapshotCreature(creature.getId());
    }

    public synchronized CreatureSnapshot changeCreatureType(String creatureId, String basisId) {
        return changeCreatureType(creatureId, resolveCreatureType(basisId));
    }

    public synchronized CreatureBasis resolveCreatureType(String basisId) {
        CreatureBasis type = library.creature.get(normalizeId(basisId));
        if (type == null) {
            throw new IllegalArgumentException("Creature type not found: " + basisId);
        }
        return type;
    }

    public synchronized boolean deleteCreature(String creatureId) {
        creatureId = normalizeId(creatureId);
        Creature removed = library.creature.unregister(creatureId);
        if (removed == null) {
            LOG.debug("Delete ignored for unknown creature {}", creatureId);
            return false;
        }
        if (Objects.equals(selectedCreatureId, creatureId)) {
            selectedCreatureId = null;
        }
        LOG.info("Deleted creature {}. New selection: {}", creatureId, selectedCreatureId);
        return true;
    }

    public synchronized CreatureSnapshot setBaseStat(String creatureId, String statId, Integer value) {
        Creature creature = requireCreature(creatureId);
        creature.getStatSet().setBaseValue(
                requireStat(statId),
                value);
        return snapshotCreature(creature.getId());
    }

    public synchronized CreatureSnapshot resetBaseStat(String creatureId, String statId) {
        Creature creature = requireCreature(creatureId);
        creature.getStatSet().resetBaseValue(requireStat(statId));
        return snapshotCreature(creature.getId());
    }

    public synchronized CreatureSnapshot removeBaseStat(String creatureId, String statId) {
        Creature creature = requireCreature(creatureId);
        creature.getStatSet().removeBaseValue(requireStat(statId));
        return snapshotCreature(creature.getId());
    }

    public synchronized CreatureSnapshot addFeature(String creatureId, String featureId) {
        FeatureInstance added = addFeatureInstance(creatureId, normalizeId(featureId));
        return added == null ? null : snapshotCreature(normalizeId(creatureId));
    }

    public synchronized CreatureSnapshot removeFeature(String creatureId, String featureInstanceId) {
        Creature creature = requireCreature(creatureId);
        FeatureInstance removed = creature.getFeatureSet().removeFeature(
                normalizeId(featureInstanceId));
        if (removed == null) {
            LOG.debug("Feature {} not found on creature {}", featureInstanceId, creature.getId());
        } else {
            LOG.debug("Removed feature {} from creature {}", removed.id, creature.getId());
        }
        return removed == null ? null : snapshotCreature(creature.getId());
    }

    public synchronized SessionSnapshot createSnapshot() {
        // Collect creature snapshots from library
        LinkedList<CreatureSnapshot> creatureSnapshots = new LinkedList<>();
        for (String creatureId : library.creature.getOwnedKeys()) {
            Creature creature = library.creature.get(creatureId);
            if (creature != null) {
                creatureSnapshots.add(snapshotCreature(creatureId));
            }
        }
        
        return new SessionSnapshot(
                SessionSnapshot.CURRENT_SCHEMA_VERSION,
                selectedCreatureId,
                nextCreatureNumber,
                creatureSnapshots
        );
    }

    public synchronized void saveWorkspaceSnapshot() throws IOException {
        LOG.debug("Saving workspace snapshot for {} with {} creatures", session.getWorkingDirectory(), library.creature.getAllKeys().size());
        session.saveWorkspacePack();
        createSnapshot().save(session.getWorkingDirectory());
    }

    /*private synchronized void saveCreaturesToWorkspace() throws IOException {
        // Collect all creatures from library
        java.util.Map<String, Creature> creatures = new java.util.HashMap<>();
        for (String creatureId : library.creature.getAllKeys()) {
            Creature creature = library.creature.get(creatureId);
            if (creature != null) {
                creatures.put(creatureId, creature);
            }
        }
        
        // Save creatures to workspace pack
        if (!creatures.isEmpty()) {
            dungeonmanager.contentpack.PackLoader.saveCreaturesToPack(session.getWorkingDirectory(), creatures);
        }
    }*/

    public synchronized boolean hasFeature(String featureId) {
        return library.feature.get(normalizeId(featureId)) != null;
    }

    /**
     * Load a feature into a creature from a snapshot, preserving its configuration.
     * @param creature the creature to add the feature to
     * @param featureSnapshot the feature snapshot to load
     * @throws IllegalArgumentException if the feature template is not registered
     */
    private void loadFeatureIntoCreature(@NotNull Creature creature, @NotNull FeatureInstanceSnapshot featureSnapshot) {
        // Look up feature from library
        Feature feature = library.feature.get(normalizeId(featureSnapshot.getFeatureId()));
        if (feature == null) {
            throw new IllegalArgumentException("Feature not registered: " + featureSnapshot.getFeatureId());
        }

        if (featureSnapshot.getSectionCount() != feature.getSectionCount()) {
            throw new IllegalArgumentException("Feature section count mismatch for " + featureSnapshot.getFeatureId()
                    + ": snapshot=" + featureSnapshot.getSectionCount()
                    + ", library=" + feature.getSectionCount());
        }

        // Add feature to creature
        FeatureInstance instance = creature.getFeatureSet().addFeature(featureSnapshot.getInstanceId(), feature);
        if (instance == null) {
            throw new IllegalArgumentException("Failed to add feature to creature: " + featureSnapshot.getFeatureId());
        }

        // Apply saved configuration (selections, etc.)
        instance.loadConfigSnapshot(featureSnapshot.getConfig());

        LOG.debug("Loaded feature {} into creature", featureSnapshot.getInstanceId());
    }

    public synchronized void loadFromSnapshot(@NotNull SessionSnapshot snapshot) {
        if (snapshot.getSchemaVersion() != SessionSnapshot.CURRENT_SCHEMA_VERSION) {
            throw new IllegalArgumentException("Unsupported schema version: " + snapshot.getSchemaVersion());
        }

        String selectedId = snapshot.getSelectedCreatureId();
        Creature selected = requireCreature(selectedId);

        nextCreatureNumber = snapshot.getNextCreatureNumber();
        selectedCreatureId = selectedId == null ? null : normalizeId(selectedId);

        LOG.info("Session restored from snapshot. Selected: {}", selectedCreatureId);
    }

    private CreatureSnapshot snapshotCreature(String creatureId) {
        return CreatureSnapshot.fromCreature(requireCreature(creatureId));
    }

    private FeatureInstance addFeatureInstance(String creatureId, String featureInstanceId) {
        Feature feature = requireFeature(featureInstanceId);
        Creature creature = requireCreature(creatureId);
        String normalizedFeatureId = normalizeId(featureInstanceId == null ? feature.getId() : featureInstanceId);
        return creature.getFeatureSet().addFeature(normalizedFeatureId, feature);
    }

    private void loadSnapshot() {
        try {
            SessionSnapshot.loadIfPresent(session.getWorkingDirectory()).ifPresent(this::loadFromSnapshot);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load session snapshot for workspace " + session.getWorkingDirectory(), e);
        }
    }

    private void applyBaseStats(Creature creature, @NotNull Map<String, Integer> baseStats) {
        for (Map.Entry<String, Integer> entry : baseStats.entrySet()) {
            creature.getStatSet().setBaseValue(entry.getKey(), entry.getValue());
        }
    }

    private Stat requireStat(String statId) {
        Stat stat = library.stat.get(normalizeId(statId));
        if (stat == null) {
            throw new IllegalArgumentException("Stat not found: " + statId);
        }
        return stat;
    }

    private Creature requireCreature(String creatureId) {
        String normalizedId = normalizeId(creatureId);
        Creature creature = library.creature.get(normalizedId);
        if (creature == null) {
            throw new IllegalArgumentException("Creature not found: " + normalizedId);
        }
        return creature;
    }

    private Feature requireFeature(String featureId) {
        String normalized = normalizeId(featureId);
        Feature feature = library.feature.get(normalized);
        if (feature == null) {
            throw new IllegalArgumentException("Feature not found: " + normalized);
        }
        return feature;
    }

    public synchronized void registerFeature(Feature feat) {
        library.feature.putLocked(feat.getId(), feat);
        LOG.debug("Added library-owned feature {} in session library", feat.getId());
    }

    public synchronized void addFeature(Feature feat) {
        library.feature.putOwned(feat.getId(), feat);
        LOG.debug("Added session-owned feature {} in session library, library size: {}",
                feat.getId(), library.feature.getAllKeys().size());
    }

    public synchronized void createStat(String id, String name, String description, int defaultValue) {
        Stat stat = new DynamicStat(normalizeId(id), normalizeName(name), description, defaultValue, "session");
        session.addStat(stat);
        LOG.debug("Added session-owned stat {}", stat.getId());
    }

    public void createStat(String id, String name, String description) {
        createStat(id, name, description, 0);
    }
}
