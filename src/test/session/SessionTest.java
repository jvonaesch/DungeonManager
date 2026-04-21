package test.session;

import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.SelectionSection;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.registry.Registries;
import dungeonmanager.session.CreatureSnapshot;
import dungeonmanager.session.FeatureInstanceSnapshot;
import dungeonmanager.session.Session;
import dungeonmanager.session.SessionSnapshot;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.stats.StatModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SessionTest focuses on feature and snapshot behavior.
 * Creature lifecycle and selection tests are in SessionCreatureTest.
 */
public class SessionTest {

    @BeforeEach
    void setUp() {
        registerStandardStats();
    }

    @Test
    @DisplayName("Apply and remove a stat modifier feature")
    void stat_modifier_test() {
        Session session = new Session();
        CreatureSnapshot created = createHero(session);

        Feature feat = buildCharismaBoostFeature();
        session.registerFeature(feat);
        assertTrue(session.hasFeature(feat.ID), "Expected feature to be registered in session catalog");

        CreatureSnapshot afterFeat = session.addFeature(created.getId(), feat.ID);
        assertNotNull(afterFeat, "Expected feature to be added");
        assertEquals(10, afterFeat.getStat("CHA"), "Expected CHA 10 after feature add");
        assertEquals(1, afterFeat.getFeatures().size(), "Expected one feature in snapshot");

        FeatureInstanceSnapshot featSnapshot = afterFeat.getFeature(feat.ID);
        assertNotNull(featSnapshot, "Expected feature snapshot lookup by instance ID");
        assertEquals(feat.ID, featSnapshot.getFeatureId(), "Expected source feature ID to match registered feature ID");

        CreatureSnapshot afterRemoval = session.removeFeature(created.getId(), feat.ID);
        assertNotNull(afterRemoval, "Expected feature removal to succeed");
        assertEquals(8, afterRemoval.getStat("CHA"), "Expected CHA 8 after feature removal");
    }

    @Test
    @DisplayName("Initialize selection section configuration")
    void selection_section_test() {
        Session session = new Session();
        CreatureSnapshot created = createHero(session);

        Feature selectableFeat = buildElementalAffinityFeature();
        session.registerFeature(selectableFeat);

        CreatureSnapshot afterSelectableFeat = session.addFeature(created.getId(), selectableFeat.ID);
        assertNotNull(afterSelectableFeat, "Expected selectable feature to be added");

        FeatureInstanceSnapshot selectableSnapshot = afterSelectableFeat.getFeature(selectableFeat.ID);
        assertNotNull(selectableSnapshot, "Expected selectable feature snapshot");
        assertTrue(selectableSnapshot.getConfig().containsKey("elemental_affinity_selection"),
                "Expected selection config key from SelectionSection#loadToInstance");
        assertTrue(selectableSnapshot.getConfigFor("elemental_affinity_selection").isEmpty(),
                "Expected empty selection by default");
    }

    @Test
    @DisplayName("Session snapshot metadata is correct")
    void snapshot_metadata_test() {
        Session session = new Session();
        CreatureSnapshot created = createHero(session);

        Feature selectableFeat = buildElementalAffinityFeature();
        session.registerFeature(selectableFeat);
        CreatureSnapshot withSelectable = session.addFeature(created.getId(), selectableFeat.ID);
        assertNotNull(withSelectable, "Expected selectable feature to be added");

        FeatureInstanceSnapshot selectableSnapshot = withSelectable.getFeature(selectableFeat.ID);
        assertNotNull(selectableSnapshot, "Expected selectable feature snapshot");

        SessionSnapshot snapshot = session.snapshot();
        assertEquals(SessionSnapshot.CURRENT_SCHEMA_VERSION, snapshot.getSchemaVersion(), "Unexpected schema version");
        assertEquals(1, snapshot.getCreatureCount(), "Expected one creature in session snapshot");
        assertNotNull(snapshot.getCreature(created.getId()), "Expected creature lookup by ID in snapshot");
    }

    @Test
    @DisplayName("Session snapshot collections are immutable")
    void snapshot_mutability_test() {
        Session session = new Session();
        CreatureSnapshot created = createHero(session);

        Feature selectableFeat = buildElementalAffinityFeature();
        session.registerFeature(selectableFeat);
        CreatureSnapshot withSelectable = session.addFeature(created.getId(), selectableFeat.ID);
        assertNotNull(withSelectable, "Expected selectable feature to be added");

        FeatureInstanceSnapshot selectableSnapshot = withSelectable.getFeature(selectableFeat.ID);
        assertNotNull(selectableSnapshot, "Expected selectable feature snapshot");

        SessionSnapshot snapshot = session.snapshot();

        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.getCreatures().add(created),
                "Expected creature list snapshot to be immutable");

        assertThrows(UnsupportedOperationException.class,
                () -> created.getStats().put("STR", 99),
                "Expected stat snapshot map to be immutable");

        assertThrows(UnsupportedOperationException.class,
                () -> created.getBaseStatOverrides().put("STR", 99),
                "Expected base stat override snapshot map to be immutable");

        assertThrows(UnsupportedOperationException.class,
                () -> selectableSnapshot.getConfig().put("elemental_affinity_selection", List.of("fire_affinity")),
                "Expected feature selection map snapshot to be immutable");

        assertThrows(UnsupportedOperationException.class,
                () -> selectableSnapshot.getConfigFor("elemental_affinity_selection").add("fire_affinity"),
                "Expected feature selection list snapshot to be immutable");
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
