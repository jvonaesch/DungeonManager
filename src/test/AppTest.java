package test;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.FeatureSerializer;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.session.Session;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.stat.StatModifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AppTest {

    protected static final Logger LOG = LoggerFactory.getLogger(AppTest.class);
    protected static final String TEST_WORKSPACE_PATH = "test/";

    protected static final DungeonManagerApp app = new DungeonManagerApp();
    protected static final Session setupSession = app.getSession(TEST_WORKSPACE_PATH);
    protected Session session;

    @BeforeAll
    static void beforeAll() {
        LOG.info("Setting up test environment...");
        registerStandardStats(setupSession);
        generateTestData();
    }

    @BeforeEach
    void setUp() {
        session = app.getSession(TEST_WORKSPACE_PATH);
        registerStandardStats(session);
    }

    static void registerStandardStats(Session session) {
        for (StandardStat stat : StandardStat.values()) {
            session.registerStat(stat);
        }
    }

    static void generateTestData() {
        Feature testFeature = new Feature(
                "test_feature_1",
                "Test Feature",
                "A feature for testing purposes."
        );
        testFeature.addSection(new StatModifierSection(
                "test_modifier",
                "Test Modifier",
                "A stat modifier for testing.",
                new StatModifier().setValue(setupSession.getStat("STR"), 2)
        ));
        String featureTargetPath = TEST_WORKSPACE_PATH + "test_pack_1/features/test_feature_1.json";
        try {
            FeatureSerializer.writeToFile(featureTargetPath, testFeature.toJson());
        } catch (IOException e) {
            LOG.error("Failed to create test feature file: {}", featureTargetPath, e);
        }
    }
}
