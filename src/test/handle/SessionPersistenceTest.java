package test.handle;

import com.fasterxml.jackson.core.JsonProcessingException;
import dungeonmanager.DungeonManagerApp;
import dungeonmanager.contentpack.PackLoader;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.session.CreatureSnapshot;
import dungeonmanager.session.SessionHandle;
import dungeonmanager.session.SessionSnapshot;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.stat.Stat;
import dungeonmanager.stat.StatModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dungeonmanager.contentpack.JsonSerializable.LOG;
import static dungeonmanager.contentpack.PackLoader.MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Session Persistence Tests")
public class SessionPersistenceTest {

    @TempDir
    Path tempDir;
    Path packDir;

    @BeforeEach
    void setup() throws IOException {
        LOG.debug("Created temporary directory for test: {}", tempDir.toAbsolutePath());
        packDir = tempDir.resolve("packs");
    }

    @Test
    @DisplayName("Creature features are restored")
    void creature_features_are_restored() throws IOException {
        Path workspace = tempDir.resolve("workspace");
        preparePacks(packDir);

        SessionHandle handle = getHandle(workspace);

        CreatureSnapshot hero = handle.createCreature("Hero", Map.of("STR", 15));
        CreatureSnapshot mage = handle.createCreature("Mage", Map.of("INT", 14));
        handle.addFeature(hero.getId(), "test_feature_1");
        handle.selectCreature(hero.getId());
        handle.saveWorkspaceSnapshot();

        LOG.debug("=== Setup session completed! Reloading... ===\n");

        DungeonManagerApp reloadedApp = new DungeonManagerApp();
        SessionHandle reloaded = reloadedApp.getSessionHandle(workspace, packDir);
        SessionSnapshot snapshot = reloaded.createSnapshot();

        Set<String> found = new HashSet<>(snapshot.getCreatures().stream().map(CreatureSnapshot::getId).toList());
        Set<String> expected = Set.of(hero.getId(), mage.getId());
        assertTrue(found.containsAll(expected) && expected.containsAll(found),
                "Expected to find same creatures after the save/load cycle");
        assertEquals(hero.getId(), snapshot.getSelectedCreatureId(), "Expected selection to survive the save/load cycle");
        assertEquals(handle.createSnapshot().getNextCreatureNumber(), snapshot.getNextCreatureNumber(),
                "Expected creature numbering to survive the save/load cycle");
        assertNotNull(handle.getCreatureSnapshot(hero.getId()).getFeature("test_feature_1"),
                "Expected loaded feature snapshot to be preserved");
    }

    @Test
    @DisplayName("Loads a fresh workspace without a session snapshot")
    void loads_a_fresh_workspace_without_session_snapshot() throws IOException {
        Path workspace = tempDir.resolve("empty-workspace");
        Files.createDirectories(workspace);

        DungeonManagerApp app = new DungeonManagerApp();
        SessionHandle handle = app.getSessionHandle(workspace);

        LOG.debug("Creatures found in fresh workspace: {}",
                handle.createSnapshot().getCreatures().stream().map(CreatureSnapshot::getId).toList());

        assertNull(handle.getSelectedCreatureId(), "Expected empty workspace to have no selection");
        assertEquals(0, handle.createSnapshot().getCreatures().size(), "Expected empty workspace not to own any local creatures");
    }

    @Test
    @DisplayName("Fails to load a session snapshot when a required feature is missing")
    void fails_to_load_snapshot_when_feature_is_missing() throws IOException {
        Path workspace = tempDir.resolve("missing-feature-workspace");
        preparePacks(packDir);

        SessionHandle handle = getHandle(workspace);

        CreatureSnapshot hero = handle.createCreature("Hero", Map.of("STR", 15));
        handle.addFeature(hero.getId(), "test_feature_1");
        handle.saveWorkspaceSnapshot();

        boolean deleted = Files.deleteIfExists(packDir.resolve("test_pack_1/features/test_feature_1.json"));
        System.out.println("\n\t=== RELOADING ===\n");

        assertThrows(IllegalArgumentException.class, () -> getHandle(workspace),
                "Expected missing feature to fail workspace restore");
    }

    @Test
    @DisplayName("Fails to load a session snapshot when a required stat is missing")
    void fails_to_load_snapshot_when_stat_is_missing() throws IOException {
        Path workspace = tempDir.resolve("missing-stat-workspace");
        preparePacks(packDir);

        SessionHandle handle = getHandle(workspace);
        handle.createCreature("Hero", Map.of("STR", 15));
        handle.saveWorkspaceSnapshot();

        Files.deleteIfExists(workspace.resolve("test_pack_1/stats.json"));

        assertThrows(IllegalArgumentException.class, () -> new DungeonManagerApp().getSessionHandle(workspace),
                "Expected missing stat to fail workspace restore");
    }

    @Test
    @DisplayName("Fails to parse session snapshots with unsupported schema versions")
    void fails_to_parse_session_snapshots_with_unsupported_schema_version() throws JsonProcessingException {
        SessionSnapshot snapshot = new SessionSnapshot(
                SessionSnapshot.CURRENT_SCHEMA_VERSION,
                null,
                1L
        );
        String json = MAPPER.writeValueAsString(snapshot.toJson())
                .replace("\"schemaVersion\" : 1", "\"schemaVersion\" : 99");

        assertThrows(IllegalArgumentException.class, () -> SessionSnapshot.fromJson(json),
                "Expected schema mismatch to fail parsing");
    }

    private void preparePacks(Path packs) throws IOException {
        Path packDir = packs.resolve("test_pack_1");
        Path featuresDir = packDir.resolve("features");
        Files.createDirectories(featuresDir);

        PackLoader.writeToFile(
                packDir.resolve("stats.json"),
                Stat.toJson(Set.of(StandardStat.values()))
        );

        Feature feature = new Feature("test_feature_1", "Test Feature", "A feature for testing purposes.")
                .addSection(new StatModifierSection(
                        "test_modifier",
                        "Test Modifier",
                        "A stat modifier for testing.",
                        new StatModifier("STR").setBaseValue(2)
                ));
        feature.storeTo(featuresDir.resolve("test_feature_1.json"));
    }

    private SessionHandle getHandle(Path workspaceDir) {
        DungeonManagerApp app = new DungeonManagerApp();
        return app.getSessionHandle(workspaceDir, packDir);
    }
}

