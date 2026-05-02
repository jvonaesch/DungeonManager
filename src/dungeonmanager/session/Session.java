package dungeonmanager.session;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.contentpack.PackLoader;
import dungeonmanager.creature.Creature;
import dungeonmanager.feature.Feature;
import dungeonmanager.library.SessionLibrary;
import dungeonmanager.stat.Stat;
import dungeonmanager.stat.StatContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;


public class Session {

    private static final Logger LOG = LoggerFactory.getLogger(Session.class);
    private final Path workingDirectory;
    private final PackLoader packLoader;
    public final SessionLibrary library = new SessionLibrary();
    private final StatContext statContext;

    public Session(DungeonManagerApp app, Path workspacePath) {
        this.workingDirectory = workspacePath;
        this.packLoader = new PackLoader(this);
        this.statContext = new StatContext(library.stat);

        LOG.debug("Loading workspace content pack from {}", workspacePath);
        packLoader.loadWorkspace(workspacePath);
        library.creature.putLocked("default", new Creature(
                statContext,
                "default",
                "Default Creature",
                null));
        LOG.debug("Session initialized");
    }

    public Session(DungeonManagerApp dungeonManagerApp, Path workspacePath, Path contentOrigin) {
        this(dungeonManagerApp, workspacePath);
        LOG.debug("Loading additional content packs from {}", contentOrigin);
        packLoader.loadPacks(contentOrigin);
        LOG.debug("Additional content pack loading complete");
        LOG.debug("library creatures: {}", library.creature.getAllKeys());
        LOG.debug("owned creatures: {}", library.feature.getOwnedKeys());
        LOG.debug("library features: {}", library.feature.getAllKeys());
        LOG.debug("owned features: {}", library.creature.getOwnedKeys());
        LOG.debug("library stats: {}", library.stat.getAllKeys());
        LOG.debug("owned stats: {}", library.stat.getOwnedKeys());
    }

    public Session(DungeonManagerApp app) {
        this(app, DungeonManagerApp.DEFAULT_WORKSPACE_PATH);
    }

    public static String normalizeId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be blank");
        }
        return normalized;
    }

    public static String normalizeName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        return normalized;
    }

    public Creature getCreature(String creatureId) {
        return library.creature.get(creatureId);
    }

    public Feature getFeature(String featureId) {
        return library.feature.get(normalizeId(featureId));
    }

    public void registerStat(@NotNull Stat stat) {
        String statId = normalizeId(stat.getId());
        library.stat.putLocked(statId, stat);
    }

    public void addStat(@NotNull Stat stat) {
        String statId = normalizeId(stat.getId());
        library.stat.putOwned(statId, stat);
    }

    public Set<String> getStatIDs() {
        return library.stat.getAllKeys();
    }

    public Stat getStat(String statId) {
        return library.stat.get(normalizeId(statId));
    }

    public StatContext getStatContext() {
        return statContext;
    }

    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Persist the current session content (creatures, features, stats) into the workspace pack.
     * This writes creatures to {@code <workspace>/creatures/*.json}, features to
     * {@code <workspace>/features/*.json} and stats to {@code <workspace>/stats.json}.
     */
    public void saveWorkspacePack() {
        try {
            Map<String, Creature> creatures = library.creature.getOwned();
            if (!creatures.isEmpty()) {
                PackLoader.saveCreaturesToPack(workingDirectory, creatures);
                LOG.info("Finished saving {} local creatures to workspace pack", creatures.size());
            } else LOG.debug("Saved no local creatures");

            Map<String, Feature> features = library.feature.getOwned();
            if (!features.isEmpty()) {
                PackLoader.saveFeaturesToPack(workingDirectory, features);
                LOG.info("Finished saving {} local features to workspace pack", features.size());
            } else LOG.debug("Saved no local features");

            Map<String, Stat> stats = library.stat.getOwned();
            if (stats != null && !stats.isEmpty()) {
                PackLoader.saveStatsToPack(workingDirectory, stats);
                LOG.info("Finished saving {} local stats to workspace pack", stats.size());
            } else LOG.debug("Saved no local stats");

        } catch (Exception e) {
            throw new IllegalStateException("Failed to save workspace pack to " + workingDirectory, e);
        }
    }
}
