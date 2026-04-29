package test.handle;

import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.SelectionSection;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.session.*;
import dungeonmanager.stat.StatModifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Session Snapshot Tests")
public class SessionSnapshotTest extends SessionHandleTest {

    @Test
    @DisplayName("Session snapshot metadata is correct")
    void snapshot_metadata_is_correct() {
        CreatureSnapshot created = createHero(handle);

        Feature selectableFeat = buildElementalAffinityFeature();
        handle.registerFeature(selectableFeat);
        CreatureSnapshot withSelectable = handle.addFeature(created.getId(), selectableFeat.getId());
        assertNotNull(withSelectable, "Expected selectable feature to be added");

        FeatureInstanceSnapshot selectableSnapshot = withSelectable.getFeature(selectableFeat.getId());
        assertNotNull(selectableSnapshot, "Expected selectable feature snapshot");

        SessionSnapshot snapshot = handle.snapshot();
        assertEquals(SessionSnapshot.CURRENT_SCHEMA_VERSION, snapshot.getSchemaVersion(), "Unexpected schema version");
        assertEquals(1, snapshot.getCreatureCount(), "Expected one creature in handle snapshot");
        assertNotNull(snapshot.getCreature(created.getId()), "Expected creature lookup by ID in snapshot");
    }

    @Test
    @DisplayName("Session snapshot collections are immutable")
    void snapshot_collections_are_immutable() {
        CreatureSnapshot created = createHero(handle);

        Feature selectableFeat = buildElementalAffinityFeature();
        handle.registerFeature(selectableFeat);
        CreatureSnapshot withSelectable = handle.addFeature(created.getId(), selectableFeat.getId());
        assertNotNull(withSelectable, "Expected selectable feature to be added");

        FeatureInstanceSnapshot selectableSnapshot = withSelectable.getFeature(selectableFeat.getId());
        assertNotNull(selectableSnapshot, "Expected selectable feature snapshot");

        SessionSnapshot snapshot = handle.snapshot();

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

    @Test
    @DisplayName("Creature snapshot stat map is immutable")
    void creature_snapshot_stats_immutable() {
        CreatureSnapshot created = createHero(handle);

        assertThrows(UnsupportedOperationException.class,
                () -> created.getStats().put("DEX", 50),
                "Expected creature stat map to be immutable");

        assertThrows(UnsupportedOperationException.class,
                () -> created.getStats().clear(),
                "Expected creature stat clear to fail");

        assertThrows(UnsupportedOperationException.class,
                () -> created.getStats().remove("STR"),
                "Expected creature stat remove to fail");
    }

    @Test
    @DisplayName("Creature snapshot base overrides map is immutable")
    void creature_snapshot_base_overrides_immutable() {
        CreatureSnapshot created = createHero(handle);

        assertThrows(UnsupportedOperationException.class,
                () -> created.getBaseStatOverrides().put("CON", 20),
                "Expected base overrides map to be immutable");

        assertThrows(UnsupportedOperationException.class,
                () -> created.getBaseStatOverrides().clear(),
                "Expected base overrides clear to fail");

        assertThrows(UnsupportedOperationException.class,
                () -> created.getBaseStatOverrides().remove("STR"),
                "Expected base overrides remove to fail");
    }

    @Test
    @DisplayName("Feature instance snapshot config is immutable")
    void feature_instance_snapshot_config_immutable() {
        CreatureSnapshot created = createHero(handle);

        Feature selectableFeat = buildElementalAffinityFeature();
        handle.registerFeature(selectableFeat);
        CreatureSnapshot withFeature = handle.addFeature(created.getId(), selectableFeat.getId());

        FeatureInstanceSnapshot featSnapshot = withFeature.getFeature(selectableFeat.getId());
        assertNotNull(featSnapshot, "Expected feature snapshot");

        assertThrows(UnsupportedOperationException.class,
                () -> featSnapshot.getConfig().put("new_key", List.of("value")),
                "Expected feature config map to be immutable");

        assertThrows(UnsupportedOperationException.class,
                () -> featSnapshot.getConfig().clear(),
                "Expected feature config clear to fail");

        assertThrows(UnsupportedOperationException.class,
                () -> featSnapshot.getConfig().remove("elemental_affinity_selection"),
                "Expected feature config remove to fail");
    }

    @Test
    @DisplayName("Feature snapshot selection lists are immutable")
    void feature_snapshot_selection_lists_immutable() {
        CreatureSnapshot created = createHero(handle);

        Feature selectableFeat = buildElementalAffinityFeature();
        handle.registerFeature(selectableFeat);
        CreatureSnapshot withFeature = handle.addFeature(created.getId(), selectableFeat.getId());

        FeatureInstanceSnapshot featSnapshot = withFeature.getFeature(selectableFeat.getId());
        assertNotNull(featSnapshot, "Expected feature snapshot");

        List<String> selections = featSnapshot.getConfigFor("elemental_affinity_selection");
        assertThrows(UnsupportedOperationException.class,
                () -> selections.add("fire_affinity"),
                "Expected selection list to be immutable");

        assertThrows(UnsupportedOperationException.class,
                () -> selections.clear(),
                "Expected selection list clear to fail");
    }

    @Test
    @DisplayName("Session snapshot creatures list is immutable")
    void session_snapshot_creatures_list_immutable() {
        CreatureSnapshot c1 = handle.createCreature("Hero1");
        CreatureSnapshot c2 = handle.createCreature("Hero2");

        SessionSnapshot snapshot = handle.snapshot();

        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.getCreatures().add(c1),
                "Expected creatures list to be immutable");

        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.getCreatures().clear(),
                "Expected creatures list clear to fail");

        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.getCreatures().remove(c1),
                "Expected creatures list remove to fail");
    }

    @Test
    @DisplayName("Snapshot reflects multiple creatures")
    void snapshot_reflects_multiple_creatures() {
        CreatureSnapshot c1 = handle.createCreature("Hero1");
        CreatureSnapshot c2 = handle.createCreature("Hero2");

        SessionSnapshot snapshot = handle.snapshot();

        assertEquals(2, snapshot.getCreatureCount(), "Expected two creatures in snapshot");
        assertNotNull(snapshot.getCreature(c1.getId()), "Expected first creature in snapshot");
        assertNotNull(snapshot.getCreature(c2.getId()), "Expected second creature in snapshot");
    }

    @Test
    @DisplayName("Snapshot preserves selected creature ID")
    void snapshot_preserves_selected_creature_id() {
        CreatureSnapshot c1 = handle.createCreature("Hero1");
        handle.createCreature("Hero2");
        handle.selectCreature(c1.getId());

        SessionSnapshot snapshot = handle.snapshot();

        assertEquals(c1.getId(), snapshot.getSelectedCreatureId(), "Expected selected creature ID in snapshot");
    }

    private CreatureSnapshot createHero(SessionHandle handle) {
        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 15);
        baseStats.put("CHA", 8);
        return handle.createCreature("Hero", IntegratedCreatureType.DEFAULT, baseStats);
    }

    private Feature buildElementalAffinityFeature() {
        return new Feature("feat:elemental_affinity", "Elemental Affinity", "Choose an element")
                .addSection(new SelectionSection("elemental_affinity_selection", "Affinity", "Choose an affinity", 1)
                        .addOption(new StatModifierSection(
                                "fire_affinity",
                                "Fire Affinity",
                                "FIRE +1",
                                new StatModifier("FIRE").setBaseValue(1),
                                false
                        )));
    }
}

