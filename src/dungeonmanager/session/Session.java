package dungeonmanager.session;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.contentpack.PackLoader;
import dungeonmanager.creature.Creature;
import dungeonmanager.creature.CreatureBasis;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.registry.SessionRegistry;
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
    private final DungeonManagerApp app;
    public final SessionRegistry registry = new SessionRegistry();


    public Session(DungeonManagerApp app, Path workspacePath) {
        this.app = app;
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
        return registry.creature.get(creatureId);
    }

    public Feature getFeature(String featureId) {
        return registry.feature.get(normalizeId(featureId));
    }

    public void registerStat(@NotNull IStat stat) {
        String statId = normalizeId(stat.getId());
        registry.stat.register(statId, stat);
    }

    public Set<String> getStatIDs() {
        return registry.stat.getAllKeys();
    }

    public IStat getStat(String statId) {
        return registry.stat.get(normalizeId(statId));
    }
}
