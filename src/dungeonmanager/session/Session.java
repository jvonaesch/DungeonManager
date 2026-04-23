package dungeonmanager.session;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.contentpack.PackLoader;
import dungeonmanager.creature.Creature;
import dungeonmanager.creature.CreatureBasis;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.registry.Registries;
import dungeonmanager.stat.IStat;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

import static dungeonmanager.DungeonManagerApp.LIB_PATH;


public class Session {

    private static final Logger LOG = LoggerFactory.getLogger(Session.class);
    private final Path workingDirectory;
    private final PackLoader packLoader;
    private record CreatureEntry(String id, Creature creature) {}
    private final DungeonManagerApp app;
    private final Registries registry = new Registries();
    private final Map<String, CreatureEntry> creatures;
    /**
     * The ID of the currently selected creature. This is null if no creature is selected.
     * <br><b>Note:</b> <i>Always</i> handle null values!
     */
    private String selectedCreatureId;
    private long nextCreatureNumber;

    public Session(DungeonManagerApp app, Path workspacePath) {
        this.app = app;
        this.creatures = new LinkedHashMap<>();
        this.nextCreatureNumber = 1L;
        this.workingDirectory = workspacePath;
        this.packLoader = new PackLoader(this);

        LOG.debug("Loading workspace content pack from {}", workspacePath);
        packLoader.loadLibrary(workspacePath);
        LOG.debug("Content pack loading complete: {} features loaded", registry.feature.getSize());
        LOG.debug("Session initialized");
    }

    public Session(DungeonManagerApp dungeonManagerApp, Path workspacePath, Set<String> contentPacks) {
        this(dungeonManagerApp, workspacePath);
        LOG.debug("Loading additional content packs: {}", contentPacks);
        for (String pack : contentPacks) {
            LOG.debug("Loading additional content pack {} from library", pack);
            packLoader.loadPack(LIB_PATH.resolve(pack));
        }
        LOG.debug("Additional content pack loading complete");
    }

    public Session(DungeonManagerApp app) {
        this(app, DungeonManagerApp.DEFAULT_WORKSPACE_PATH);
    }

    static String normalizeId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be blank");
        }
        return normalized;
    }

    static String normalizeName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        return normalized;
    }

    public synchronized CreatureSnapshot createCreature(String name) {
        return createCreature(name, IntegratedCreatureType.DEFAULT, null);
    }

    public synchronized CreatureSnapshot createCreature(String name, CreatureBasis type) {
        return createCreature(name, type, null);
    }

    public synchronized CreatureSnapshot createCreature(String name, CreatureBasis type, Map<String, Integer> baseStats) {
        type = Requires.getOrDefault(type, IntegratedCreatureType.DEFAULT);
        String creatureId = normalizeName(name).toLowerCase().replaceAll("\\s+", "_");
        if (creatures.containsKey(creatureId)) creatureId += "_" + nextCreatureNumber++;

        Creature creature = new Creature(creatureId, normalizeName(name), type);
        if (baseStats != null) applyBaseStats(creature, baseStats);

        CreatureEntry entry = new CreatureEntry(creatureId, creature);
        creatures.put(creatureId, entry);
        selectedCreatureId = creatureId;

        LOG.info("Created creature {} named '{}' as type {}", creatureId, creature.getName(), creature.getType().getID());
        return snapshotCreature(creatureId);
    }

    public synchronized boolean selectCreature(String creatureId) {
        creatureId = normalizeId(creatureId);
        if (!creatures.containsKey(creatureId)) {
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
        return creatures.containsKey(normalizeId(creatureId));
    }

    public synchronized CreatureSnapshot renameCreature(String creatureId, String name) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.rename(normalizeName(name));
        LOG.debug("Renamed creature {} to '{}'", entry.id, entry.creature.getName());
        return snapshotCreature(entry.id);
    }

    public synchronized CreatureSnapshot changeCreatureType(String creatureId, CreatureBasis type) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.changeType(Requires.requireNonNull(type, "Creature type"));
        LOG.debug("Changed creature {} type to {}", entry.id, entry.creature.getType().getID());
        return snapshotCreature(entry.id);
    }

    public synchronized boolean deleteCreature(String creatureId) {
        creatureId = normalizeId(creatureId);
        CreatureEntry removed = creatures.remove(creatureId);
        if (removed == null) {
            LOG.debug("Delete ignored for unknown creature {}", creatureId);
            return false;
        }
        if (Objects.equals(selectedCreatureId, creatureId)) {
            selectedCreatureId = creatures.isEmpty() ? null : creatures.keySet().iterator().next();
        }
        LOG.info("Deleted creature {}. New selection: {}", creatureId, selectedCreatureId);
        return true;
    }

    public synchronized CreatureSnapshot setBaseStat(String creatureId, String statId, Integer value) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.getStatSet().setBaseValue(
                registry.stat.get(normalizeId(statId)),
                value);
        return snapshotCreature(entry.id);
    }

    public synchronized CreatureSnapshot resetBaseStat(String creatureId, String statId) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.getStatSet().resetBaseValue(registry.stat.get(normalizeId(statId)));
        return snapshotCreature(entry.id);
    }

    public synchronized CreatureSnapshot removeBaseStat(String creatureId, String statId) {
        CreatureEntry entry = requireCreature(creatureId);
        entry.creature.getStatSet().removeBaseValue(registry.stat.get(normalizeId(statId)));
        return snapshotCreature(entry.id);
    }

    public synchronized CreatureSnapshot addFeature(String creatureId, String featureId) {
        FeatureInstance added = addFeatureInstance(creatureId, normalizeId(featureId));
        return added == null ? null : snapshotCreature(normalizeId(creatureId));
    }

    public synchronized CreatureSnapshot removeFeature(String creatureId, String featureInstanceId) {
        CreatureEntry entry = requireCreature(creatureId);
        FeatureInstance removed = entry.creature.getFeatureSet().removeFeature(
                normalizeId(featureInstanceId));
        if (removed == null) {
            LOG.debug("Feature {} not found on creature {}", featureInstanceId, entry.id);
        } else {
            LOG.debug("Removed feature {} from creature {}", removed.ID, entry.id);
        }
        return removed == null ? null : snapshotCreature(entry.id);
    }

    public synchronized SessionSnapshot snapshot() {
        List<CreatureSnapshot> creatureSnapshots = new ArrayList<>();
        for (CreatureEntry entry : creatures.values()) {
            creatureSnapshots.add(toCreatureSnapshot(entry));
        }
        return new SessionSnapshot(
                SessionSnapshot.CURRENT_SCHEMA_VERSION,
                selectedCreatureId,
                creatureSnapshots,
                nextCreatureNumber
        );
    }

    public synchronized void registerFeature(@NotNull Feature feature) {
        String featureId = normalizeId(feature.getId());
        registry.feature.register(featureId, feature);
    }

    public synchronized void registerFeature(String featureId, @NotNull Supplier<Feature> featureSupplier) {
        registry.feature.register(featureId, featureSupplier);
    }

    public synchronized Set<String> getFeatureIDs() {
        return registry.feature.getAllKeys();
    }

    public synchronized Feature getFeature(String featureId) {
        return registry.feature.get(normalizeId(featureId));
    }

    public synchronized void registerStat(@NotNull IStat stat) {
        String statId = normalizeId(stat.getId());
        registry.stat.register(statId, stat);
    }

    public synchronized Set<String> getStatIDs() {
        return registry.stat.getAllKeys();
    }

    public synchronized IStat getStat(String statId) {
        return registry.stat.get(normalizeId(statId));
    }

    public synchronized boolean hasFeature(String featureId) {
        return registry.feature.get(normalizeId(featureId)) != null;
    }

    private FeatureInstance addFeatureInstance(String creatureId, String featureInstanceId) {
        Feature feature = requireFeature(featureInstanceId);
        CreatureEntry entry = requireCreature(creatureId);
        String normalizedFeatureId = normalizeId(featureInstanceId == null ? feature.getId() : featureInstanceId);
        return entry.creature.getFeatureSet().addFeature(normalizedFeatureId, feature);
    }

    /**
     * Load a session from a snapshot. Restores creature IDs, order, selected creature, and nextCreatureNumber.
     * All creatures and their features/stat are reconstructed from the snapshot data.
     * @param snapshot the session snapshot to load from
     * @throws IllegalArgumentException if a required creature type or feature is not registered
     */
    public synchronized void loadFromSnapshot(@NotNull SessionSnapshot snapshot) {
        if (snapshot.getSchemaVersion() != SessionSnapshot.CURRENT_SCHEMA_VERSION) {
            throw new IllegalArgumentException("Unsupported schema version: " + snapshot.getSchemaVersion());
        }

        // Clear existing state
        creatures.clear();
        selectedCreatureId = null;
        nextCreatureNumber = 1L;

        // Restore nextCreatureNumber
        this.nextCreatureNumber = snapshot.getNextCreatureNumber();

        // Restore creatures in order
        for (CreatureSnapshot creatureSnapshot : snapshot.getCreatures()) {
            loadCreatureFromSnapshot(creatureSnapshot);
        }

        // Restore selected creature ID
        this.selectedCreatureId = snapshot.getSelectedCreatureId();

        LOG.info("Session restored from snapshot with {} creatures. Selected: {}", 
                creatures.size(), selectedCreatureId);
    }

    /**
     * Load a single creature from a snapshot and add it to the session with its original ID.
     * @param creatureSnapshot the creature snapshot to load
     * @throws IllegalArgumentException if the creature type is not registered
     */
    private void loadCreatureFromSnapshot(@NotNull CreatureSnapshot creatureSnapshot) {
        String creatureId = creatureSnapshot.getId();
        
        // Look up creature type from registry
        CreatureBasis type = registry.entityType.get(creatureSnapshot.getSourceId());
        if (type == null) {
            throw new IllegalArgumentException("Creature type not registered: " + creatureSnapshot.getSourceId());
        }

        // Create creature with type
        Creature creature = new Creature(creatureSnapshot.getId(), creatureSnapshot.getName(), type);
        
        // Apply base stat overrides
        for (Map.Entry<String, Integer> override : creatureSnapshot.getBaseStatOverrides().entrySet()) {
            IStat stat = registry.stat.get(normalizeId(override.getKey()));
            creature.getStatSet().setBaseValue(stat, override.getValue());
        }

        // Recreate features with their saved configuration
        for (FeatureInstanceSnapshot featureSnapshot : creatureSnapshot.getFeatures()) {
            loadFeatureIntoCreature(creature, featureSnapshot);
        }

        // Store the creature with its original ID
        CreatureEntry entry = new CreatureEntry(creatureId, creature);
        creatures.put(creatureId, entry);

        LOG.debug("Loaded creature {} named '{}' with {} features", 
                creatureId, creature.getName(), creatureSnapshot.getFeatures().size());
    }

    /**
     * Load a feature into a creature from a snapshot, preserving its configuration.
     * @param creature the creature to add the feature to
     * @param featureSnapshot the feature snapshot to load
     * @throws IllegalArgumentException if the feature template is not registered
     */
    private void loadFeatureIntoCreature(@NotNull Creature creature, @NotNull FeatureInstanceSnapshot featureSnapshot) {
        // Look up feature from registry
        Feature feature = registry.feature.get(featureSnapshot.getFeatureId());
        if (feature == null) {
            throw new IllegalArgumentException("Feature not registered: " + featureSnapshot.getFeatureId());
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

    private CreatureSnapshot snapshotCreature(String creatureId) {
        CreatureEntry entry = requireCreature(creatureId);
        return toCreatureSnapshot(entry);
    }

    private CreatureSnapshot toCreatureSnapshot(CreatureEntry entry) {
        return CreatureSnapshot.fromCreature(entry.id, entry.creature);
    }

    private void applyBaseStats(Creature creature, @NotNull Map<String, Integer> baseStats) {
        for (Map.Entry<String, Integer> entry : baseStats.entrySet()) {
            IStat stat = registry.stat.get(normalizeId(entry.getKey()));
            if (stat == null) {
                throw new IllegalArgumentException("Stat not found: " + entry.getKey());
            }
            creature.getStatSet().setBaseValue(stat, entry.getValue());
        }
    }

    private CreatureEntry requireCreature(String creatureId) {
        return Requires.requireById(
                creatures,
                creatureId,
                id -> new IllegalArgumentException("Creature not found: " + id)
        );
    }

    private Feature requireFeature(String featureId) {
        String normalized = normalizeId(featureId);
        Feature feature = registry.feature.get(normalized);
        if (feature == null) {
            throw new IllegalArgumentException("Feature not found: " + normalized);
        }
        return feature;
    }

    private String nextCreatureId() {
        return "creature_" + nextCreatureNumber++;
    }
}
