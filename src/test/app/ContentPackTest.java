package test.app;

import dungeonmanager.feature.Feature;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.stat.Stat;
import org.junit.jupiter.api.*;
import test.AppTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Content Pack Tests")
public class ContentPackTest extends AppTest {

    @Test
    @DisplayName("Check that standard stats were loaded from generated test content pack")
    void testLoadStandardStats() {
        for (Stat stat: StandardStat.values()) {
            assertTrue(session.getStatIDs().contains(stat.getId()), "Expected standard stat '" + stat.getId() + "' to be registered");
        }
    }

    @Test
    @DisplayName("Check that test_feature_1 was loaded from generated test content pack")
    void testLoadDummyFeature() {
        assertTrue(session.getFeatureIDs().contains("test_feature_1"), "Expected 'test_feature_1' to be registered");
        Feature feature = session.getFeature("test_feature_1");

        assertEquals(1, feature.getSections().size(), "Expected 'test_feature_1' to have 1 section");
        assertTrue(feature.getSections().get(0) instanceof StatModifierSection, "Expected section to be a StatModifierSection");
        StatModifierSection section = (StatModifierSection) feature.getSections().get(0);
        assertEquals(2, section.getModifier().getValue(session.getStat("STR")), "Expected STR modifier to be 2");

        session.getFeatureIDs().forEach(key -> {
            LOG.debug("found feature: " + key);
        });
    }
}
