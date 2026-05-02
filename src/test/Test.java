package test;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.session.CreatureSnapshot;
import dungeonmanager.session.Session;
import dungeonmanager.session.SessionHandle;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.stat.Stat;
import dungeonmanager.stat.StatModifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static dungeonmanager.contentpack.PackLoader.writeToFile;

public abstract class Test {
    protected static final Logger LOG = LoggerFactory.getLogger(Test.class);
    public static final Path TEST_WORKSPACE_PATH = Path.of("test/");

    static {
        try {
            Files.deleteIfExists(TEST_WORKSPACE_PATH.resolve("session.json"));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to clear the shared test session snapshot", e);
        }
    }

    protected static final DungeonManagerApp app = new DungeonManagerApp();
    protected static final Session setupSession = new Session(app, TEST_WORKSPACE_PATH);

    protected static Session session;
    protected static SessionHandle handle;

    @BeforeEach
    public void beforeEach() {
        session = new Session(app, TEST_WORKSPACE_PATH);
        handle = new SessionHandle(session);
    }

    @BeforeAll
    static void beforeAll() {
        LOG.info("Setting up test environment...");
        generateTestData();
    }

    protected static void generateTestData() {
        for (StandardStat stat : StandardStat.values()) {
            setupSession.registerStat(stat);
        }
        Feature testFeature = new Feature(
                "test_feature_1",
                "Test Feature",
                "A feature for testing purposes."
        );
        testFeature.addSection(new StatModifierSection(
                "test_modifier",
                "Test Modifier",
                "A stat modifier for testing.",
                new StatModifier("STR").setBaseValue(2)
        ));
        try {
            writeToFile(
                    TEST_WORKSPACE_PATH.resolve("test_pack_1/stats.json"),
                    Stat.toJson(Set.of(StandardStat.values())));
            LOG.debug("Created test stats file at {}", TEST_WORKSPACE_PATH.resolve("test_pack_1/stats.json"));
            testFeature.storeTo(TEST_WORKSPACE_PATH.resolve("test_pack_1/features/test_feature_1.json"));
        } catch (IOException e) {
            LOG.error("Failed to create test files: e", e);
        }
    }

    private CreatureSnapshot createHero() {
        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 15);
        baseStats.put("CHA", 8);
        return handle.createCreature("Hero", "default", baseStats);
    }
}
