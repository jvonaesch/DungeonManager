package test.app;

import dungeonmanager.feature.Feature;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.stat.Stat;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
        Set<String> allKeys = session.registry.feature.getAllKeys();
        assertTrue(allKeys.contains("test_feature_1"), "Expected 'test_feature_1' to be registered");
        Feature feature = session.getFeature("test_feature_1");

        assertEquals(1, feature.getSections().size(), "Expected 'test_feature_1' to have 1 section");
        assertInstanceOf(StatModifierSection.class, feature.getSections().getFirst(), "Expected section to be a StatModifierSection");
        StatModifierSection section = (StatModifierSection) feature.getSections().getFirst();
        assertEquals(section.getModifier().getTargetStatId(), "STR", "Expected STR to be the target stat");
        assertEquals(section.getModifier().getBaseValue(), 2, "Expected base value to be 2");

        allKeys.forEach(key -> LOG.debug("found feature: {}", key));
    }
}
