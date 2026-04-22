package test.session;

import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.SelectionSection;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.registry.Registries;
import dungeonmanager.session.CreatureSnapshot;
import dungeonmanager.session.FeatureInstanceSnapshot;
import dungeonmanager.session.Session;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.stats.StatModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Session Feature Tests")
public class SessionFeatureTest {

    @BeforeEach
    void setUp() {
        registerStandardStats();
    }

    @Test
    @DisplayName("Registers a feature")
    void registers_feature() {
        Session session = new Session();
        Feature feat = buildCharismaBoostFeature();

        session.registerFeature(feat);

        assertTrue(session.hasFeature(feat.getId()), "Expected feature to be registered");
    }

    @Test
    @DisplayName("Apply and remove a stat modifier feature")
    void applies_and_removes_stat_modifier_feature() {
        Session session = new Session();
        CreatureSnapshot created = createHero(session);

        Feature feat = buildCharismaBoostFeature();
        session.registerFeature(feat);
        assertTrue(session.hasFeature(feat.getId()), "Expected feature to be registered in session catalog");

        CreatureSnapshot afterFeat = session.addFeature(created.getId(), feat.getId());
        assertNotNull(afterFeat, "Expected feature to be added");
        assertEquals(10, afterFeat.getStat("CHA"), "Expected CHA 10 after feature add");
        assertEquals(1, afterFeat.getFeatures().size(), "Expected one feature in snapshot");

        FeatureInstanceSnapshot featSnapshot = afterFeat.getFeature(feat.getId());
        assertNotNull(featSnapshot, "Expected feature snapshot lookup by instance ID");
        assertEquals(feat.getId(), featSnapshot.getFeatureId(), "Expected source feature ID to match registered feature ID");

        CreatureSnapshot afterRemoval = session.removeFeature(created.getId(), feat.getId());
        assertNotNull(afterRemoval, "Expected feature removal to succeed");
        assertEquals(8, afterRemoval.getStat("CHA"), "Expected CHA 8 after feature removal");
    }

    @Test
    @DisplayName("Initialize selection section configuration")
    void initializes_selection_section_config() {
        Session session = new Session();
        CreatureSnapshot created = createHero(session);

        Feature selectableFeat = buildElementalAffinityFeature();
        session.registerFeature(selectableFeat);

        CreatureSnapshot afterSelectableFeat = session.addFeature(created.getId(), selectableFeat.getId());
        assertNotNull(afterSelectableFeat, "Expected selectable feature to be added");

        FeatureInstanceSnapshot selectableSnapshot = afterSelectableFeat.getFeature(selectableFeat.getId());
        assertNotNull(selectableSnapshot, "Expected selectable feature snapshot");
        assertTrue(selectableSnapshot.getConfig().containsKey("elemental_affinity_selection"),
                "Expected selection config key from SelectionSection#loadToInstance");
        assertTrue(selectableSnapshot.getConfigFor("elemental_affinity_selection").isEmpty(),
                "Expected empty selection by default");
    }

    @Test
    @DisplayName("Apply multiple features to a creature")
    void applies_multiple_features() {
        Session session = new Session();
        CreatureSnapshot created = createHero(session);

        Feature feat1 = buildCharismaBoostFeature();
        Feature feat2 = buildElementalAffinityFeature();
        session.registerFeature(feat1);
        session.registerFeature(feat2);

        CreatureSnapshot afterFeat1 = session.addFeature(created.getId(), feat1.getId());
        assertEquals(1, afterFeat1.getFeatures().size(), "Expected one feature");

        CreatureSnapshot afterFeat2 = session.addFeature(created.getId(), feat2.getId());
        assertEquals(2, afterFeat2.getFeatures().size(), "Expected two features");
    }

    private CreatureSnapshot createHero(Session session) {
        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 15);
        baseStats.put("CHA", 8);
        return session.createCreature("Hero", IntegratedCreatureType.DEFAULT, baseStats);
    }

    private Feature buildCharismaBoostFeature() {
        return new Feature("feat:charisma_boost", "Charisma Boost", "Gain charisma")
                .addSection(new StatModifierSection(
                        "cha_bonus",
                        "CHA Bonus",
                        "CHA +2",
                        new StatModifier().setValue(StandardStat.CHA, 2),
                        false
                ));
    }

    private Feature buildElementalAffinityFeature() {
        return new Feature("feat:elemental_affinity", "Elemental Affinity", "Choose an element")
                .addSection(new SelectionSection("elemental_affinity_selection", "Affinity", "Choose an affinity", 1)
                        .addOption(new StatModifierSection(
                                "fire_affinity",
                                "Fire Affinity",
                                "FIRE +1",
                                new StatModifier().setValue("FIRE", 1),
                                false
                        )));
    }

    private void registerStandardStats() {
        for (StandardStat stat : StandardStat.values()) {
            Registries.get().stats.register(stat.getID(), stat);
        }
    }
}



