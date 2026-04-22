package test.app;

import dungeonmanager.feature.Feature;
import dungeonmanager.feature.StatModifierSection;
import org.junit.jupiter.api.*;
import test.AppTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Content Pack Tests")
public class ContentPackTest extends AppTest {

    @Test
    @DisplayName("Creates creature with default type")
    void testLoadDummyFeature() {
        session.getFeatureIDs().forEach(key -> {
            LOG.debug("Registered feature: " + key);
            assertTrue(session.getFeatureIDs().contains("test_feature"), "Expected 'test_feature' to be registered");
            if (key.equals("test_feature")) {
                Feature feature = session.getFeature(key);
                assertEquals(1, feature.getSections().size(), "Expected 'test_feature' to have 1 section");
                assertTrue(feature.getSections().get(0) instanceof StatModifierSection, "Expected section to be a StatModifierSection");
                StatModifierSection section = (StatModifierSection) feature.getSections().get(0);
                assertEquals(2, section.getModifier().getValue(session.getStat("STR")), "Expected STR modifier to be 2");
            }
        });
    }
}
