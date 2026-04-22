package test.app;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.FeatureSerializer;
import dungeonmanager.feature.Features;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.registry.Registries;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StatModifier;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Content Pack Tests")
public class ContentPackTest {

    static final Logger LOG = LoggerFactory.getLogger(ContentPackTest.class);

    static final String USER_DIR = System.getProperty("user.home");
    static final String APP_PATH = USER_DIR + "/DungeonManager/";
    static final String LIB_PATH = APP_PATH + "library/";
    static final String TEST_WORKSPACE_PATH = APP_PATH + "workspace/test/";

    DungeonManagerApp app;
    private static final Registries registry = Registries.get();

    @BeforeAll
    static void beforeAll() {
        LOG.info("Setting up test environment...");
        for (Stat stat : StandardStat.values()) {
            registry.stats.register(stat.getID(), stat);
        }
        Feature testFeature = new Feature(
                "test_feature",
                "Test Feature",
                "A feature for testing purposes."
        );
        testFeature.addSection(new StatModifierSection(
                "test_modifier",
                "Test Modifier",
                "A stat modifier for testing.",
                new StatModifier().setValue(registry.stats.get("STR"), 2)
        ));
        String featureTargetPath = TEST_WORKSPACE_PATH + "features/test_feature_1.json";
        try {
            FeatureSerializer.writeToFile(featureTargetPath, testFeature.toJson());
        } catch (IOException e) {
            LOG.error("Failed to create test feature file: {}", featureTargetPath, e);
        }
    }

    @BeforeEach
    void setUp() {
        app = new DungeonManagerApp(TEST_WORKSPACE_PATH);
    }

    @Test
    @DisplayName("Creates creature with default type")
    void testLoadDummyFeature() {
        registry.feature.getAllKeys().forEach(key -> {
            LOG.debug("Registered feature: " + key);
            assertTrue(registry.feature.getAllKeys().contains("test_feature"), "Expected 'test_feature' to be registered");
            if (key.equals("test_feature")) {
                Feature feature = registry.feature.get(key);
                assertEquals(1, feature.getSections().size(), "Expected 'test_feature' to have 1 section");
                assertTrue(feature.getSections().get(0) instanceof StatModifierSection, "Expected section to be a StatModifierSection");
                StatModifierSection section = (StatModifierSection) feature.getSections().get(0);
                assertEquals(2, section.getModifier().getValue(registry.stats.get("STR")), "Expected STR modifier to be 2");
            }
        });
    }
}
