package test.handle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Loads placeholder feature if feature not found")
    void loads_placeholder_if_feature_not_found() throws IOException {
        Path workspace = tempDir.resolve("missing-feature-workspace");
        preparePacks(packDir);

        SessionHandle handle = getHandle(workspace);

        CreatureSnapshot hero = handle.createCreature("Hero", Map.of("STR", 15));
        hero = handle.addFeature(hero.getId(), "test_feature_1");
        String actualName = hero.getFeature("test_feature_1").getName();
        handle.saveWorkspaceSnapshot();

        boolean deleted = Files.deleteIfExists(packDir.resolve("test_pack_1/features/test_feature_1.json"));
        System.out.println("\n\t=== RELOADING ===\n");
        SessionHandle new_handle = getHandle(workspace);
        CreatureSnapshot new_hero = new_handle.getCreatureSnapshot(hero.getId());

        LOG.debug("Expecting placeholder: '{}' - {}",
                new_hero.getFeature("test_feature_1").getName(),
                new_hero.getFeature("test_feature_1").getDescription());
        assertNotEquals(actualName, new_hero.getFeature("test_feature_1").getName(),
                "Expected missing feature to be filled in with a placeholder");
        assertEquals(15, new_hero.getStat("STR"),
                "Expected stat modifiers from the missing feature to not be applied to the creature");

        new_handle.addFeature(getTestFeature());
        assertTrue(handle.hasFeature("test_feature_1"),
                "Expected session to now have the missing feature available");
        new_hero = new_handle.getCreatureSnapshot(hero.getId());
        LOG.debug("Expecting feature: '{}' - {}",
                new_hero.getFeature("test_feature_1").getName(),
                new_hero.getFeature("test_feature_1").getDescription());
        assertEquals(actualName, new_hero.getFeature("test_feature_1").getName(),
                "Expected placeholder to be replaced with real feature when it becomes available");
        assertEquals(17, new_hero.getStat("STR"),
                "Expected stat modifiers from the newly added feature to be applied to the creature");

    }

    @Test
    @DisplayName("Missing stats are null")
    void missing_stats_fall_back_to_zero() throws IOException {
        Path workspace = tempDir.resolve("missing-stat-workspace");
        preparePacks(packDir);

        SessionHandle handle = getHandle(workspace);
        CreatureSnapshot hero = handle.createCreature("Hero", Map.of("STR", 15));
        handle.saveWorkspaceSnapshot();

        System.out.println("\n=== RELOAD ===\n");
        Files.deleteIfExists(packDir.resolve("test_pack_1/stats.json"));
        SessionHandle reloaded = new DungeonManagerApp().getSessionHandle(workspace, packDir);

        CreatureSnapshot hero_rebooted = reloaded.getCreatureSnapshot(hero.getId());
        assertEquals(null, hero_rebooted.getStat("STR"));
        assertEquals(null, hero_rebooted.getStat("INT"));
    }

    @Test
    @DisplayName("Fails to parse session snapshots with unsupported schema versions")
    void fails_to_parse_session_snapshots_with_unsupported_schema_version() throws JsonProcessingException {
        SessionSnapshot snapshot = new SessionSnapshot(
                SessionSnapshot.CURRENT_SCHEMA_VERSION,
                null,
                1L
        );
        ObjectNode json = (ObjectNode) snapshot.toJson();
        json.put("schemaVersion", 99);

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

        Feature feature = getTestFeature();
        feature.storeTo(featuresDir.resolve("test_feature_1.json"));
    }

    Feature getTestFeature() {
        return new Feature("test_feature_1", "Test Feature", "A feature for testing purposes.")
                .addSection(new StatModifierSection(
                        "test_modifier",
                        "Test Modifier",
                        "A stat modifier for testing.",
                        new StatModifier("STR").setBaseValue(2)
                ));
    }

    private SessionHandle getHandle(Path workspaceDir) {
        DungeonManagerApp app = new DungeonManagerApp();
        return app.getSessionHandle(workspaceDir, packDir);
    }
}

