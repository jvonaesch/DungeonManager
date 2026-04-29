package test.handle;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.session.Session;
import dungeonmanager.session.SessionHandle;
import dungeonmanager.stat.Stat;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.stat.StatModifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static dungeonmanager.contentpack.PackLoader.writeToFile;

public abstract class SessionHandleTest {

    protected static final Logger LOG = LoggerFactory.getLogger(test.app.AppTest.class);
    protected static final Path TEST_WORKSPACE_PATH = Path.of("test/");

    protected static final DungeonManagerApp app = new DungeonManagerApp();
    protected static final Session setupSession = new Session(app, TEST_WORKSPACE_PATH);
    protected SessionHandle handle;

    @BeforeAll
    static void beforeAll() {
        LOG.info("Setting up test environment...");
        generateTestData();
    }

    @BeforeEach
    void setUp() {
        handle = app.getSessionHandle(TEST_WORKSPACE_PATH);
    }

    static void generateTestData() {
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
                    TEST_WORKSPACE_PATH.resolve("/test_pack_1/stats.json"),
                    Stat.toJson(Set.of(StandardStat.values())));
            testFeature.storeTo(TEST_WORKSPACE_PATH.resolve("/test_pack_1/features/test_feature_1.json"));
        } catch (IOException e) {
            LOG.error("Failed to create test files: e", e);
        }
    }
}

