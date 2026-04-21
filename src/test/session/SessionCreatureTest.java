package test.session;

import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.registry.Registries;
import dungeonmanager.session.CreatureSnapshot;
import dungeonmanager.session.Session;
import dungeonmanager.stats.StandardStat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Session Creature Lifecycle Tests")
public class SessionCreatureTest {

    @BeforeEach
    void setUp() {
        registerStandardStats();
    }

    @Test
    @DisplayName("Creates creature with default type")
    void creates_creature_with_default_type() {
        Session session = new Session();
        CreatureSnapshot creature = session.createCreature("Ranger");

        assertNotNull(creature, "Expected creature to be created");
        assertEquals("Ranger", creature.getName(), "Expected creature name to match");
        assertEquals(IntegratedCreatureType.DEFAULT.getID(), creature.getTypeId(), "Expected default type");
        assertNotNull(creature.getId(), "Expected creature to have an ID");
        assertEquals(creature.getId(), session.getSelectedCreatureId(), "Expected new creature to be auto-selected");
    }

    @Test
    @DisplayName("Creates creature with specified type")
    void creates_creature_with_specified_type() {
        Session session = new Session();
        CreatureSnapshot creature = session.createCreature("Bear", IntegratedCreatureType.OWLBEAR);

        assertNotNull(creature, "Expected creature to be created");
        assertEquals("Bear", creature.getName(), "Expected creature name to match");
        assertEquals(IntegratedCreatureType.OWLBEAR.getID(), creature.getTypeId(), "Expected OWLBEAR type");
    }

    @Test
    @DisplayName("Creates creature with base stat overrides")
    void creates_creature_with_base_stat_overrides() {
        Session session = new Session();
        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 18);
        baseStats.put("DEX", 10);
        baseStats.put("CON", 16);

        CreatureSnapshot creature = session.createCreature("Barbarian", IntegratedCreatureType.DEFAULT, baseStats);

        assertNotNull(creature, "Expected creature to be created");
        assertEquals(18, creature.getStat("STR"), "Expected STR 18");
        assertEquals(10, creature.getStat("DEX"), "Expected DEX 10");
        assertEquals(16, creature.getStat("CON"), "Expected CON 16");
        assertEquals(Integer.valueOf(18), creature.getBaseStatOverrides().get("STR"), "Expected STR to be overridden");
    }

    @Test
    @DisplayName("Renames a creature")
    void renames_creature() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Hero");
        String creatureId = created.getId();

        CreatureSnapshot renamed = session.renameCreature(creatureId, "Legend");

        assertEquals("Legend", renamed.getName(), "Expected renamed creature");
        assertEquals(created.getId(), renamed.getId(), "Expected ID to remain unchanged");
    }

    @Test
    @DisplayName("Changes creature type")
    void changes_creature_type() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Beast", IntegratedCreatureType.DEFAULT);
        String creatureId = created.getId();

        CreatureSnapshot changed = session.changeCreatureType(creatureId, IntegratedCreatureType.DWARF);

        assertEquals(IntegratedCreatureType.DWARF.getID(), changed.getTypeId(), "Expected creature type to change");
        assertEquals(created.getId(), changed.getId(), "Expected ID to remain unchanged");
        
        // Verify that DWARF's base stats were applied
        assertNotNull(changed.getStats(), "Expected stats map to exist");
        for (StandardStat stat : StandardStat.values()) {
            int expectedStat = IntegratedCreatureType.DWARF.getStatSet().getValue(stat);
            int actualStat = changed.getStat(stat.getID());
            assertEquals(expectedStat, actualStat, "Expected " + stat.getID() + " to match DWARF type: " + expectedStat);
        }
    }

    @Test
    @DisplayName("Sets base stat on creature")
    void sets_base_stat() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Warrior");
        String creatureId = created.getId();

        CreatureSnapshot edited = session.setBaseStat(creatureId, "STR", 20);

        assertEquals(20, edited.getStat("STR"), "Expected STR to be set to 20");
        assertEquals(Integer.valueOf(20), edited.getBaseStatOverrides().get("STR"), "Expected base override to be set");
    }

    @Test
    @DisplayName("Resets base stat on creature")
    void resets_base_stat() {
        Session session = new Session();
        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 18);
        CreatureSnapshot created = session.createCreature("Knight", IntegratedCreatureType.DEFAULT, baseStats);
        String creatureId = created.getId();

        CreatureSnapshot reset = session.resetBaseStat(creatureId, "STR");

        assertNull(reset.getBaseStatOverrides().get("STR"), "Expected base override to be reset");
    }

    @Test
    @DisplayName("Removes base stat on creature")
    void removes_base_stat() {
        Session session = new Session();
        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 18);
        CreatureSnapshot created = session.createCreature("Paladin", IntegratedCreatureType.DEFAULT, baseStats);
        String creatureId = created.getId();

        CreatureSnapshot removed = session.removeBaseStat(creatureId, "STR");

        assertNull(removed.getBaseStatOverrides().get("STR"), "Expected base override to be removed");
    }

    @Test
    @DisplayName("Selects a creature by ID")
    void selects_creature_by_id() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Hero");
        String creatureId = created.getId();
        session.clearSelection();

        assertTrue(session.selectCreature(creatureId), "Expected selection to succeed");
        assertEquals(creatureId, session.getSelectedCreatureId(), "Expected creature to be selected");
    }

    @Test
    @DisplayName("Selection fails for unknown creature")
    void selection_fails_for_unknown_creature() {
        Session session = new Session();
        session.createCreature("Hero");

        assertFalse(session.selectCreature("unknown-id"), "Expected selection to fail");
    }

    @Test
    @DisplayName("Gets selected creature snapshot")
    void gets_selected_creature_snapshot() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Hero");

        CreatureSnapshot selected = session.getSelectedCreatureSnapshot();

        assertNotNull(selected, "Expected selected creature snapshot");
        assertEquals(created.getId(), selected.getId(), "Expected correct creature snapshot");
    }

    @Test
    @DisplayName("Gets selected creature snapshot when none selected")
    void gets_selected_creature_snapshot_when_none_selected() {
        Session session = new Session();
        session.createCreature("Hero");
        session.clearSelection();

        CreatureSnapshot selected = session.getSelectedCreatureSnapshot();

        assertNull(selected, "Expected null when no creature selected");
    }

    @Test
    @DisplayName("Gets creature snapshot by ID")
    void gets_creature_snapshot_by_id() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Hero");
        String creatureId = created.getId();
        session.clearSelection();

        CreatureSnapshot retrieved = session.getCreatureSnapshot(creatureId);

        assertNotNull(retrieved, "Expected creature snapshot");
        assertEquals(creatureId, retrieved.getId(), "Expected correct creature snapshot");
    }

    @Test
    @DisplayName("Checks if creature exists")
    void checks_if_creature_exists() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Hero");
        String creatureId = created.getId();

        assertTrue(session.hasCreature(creatureId), "Expected creature to exist");
        assertFalse(session.hasCreature("unknown-id"), "Expected unknown creature to not exist");
    }

    @Test
    @DisplayName("Deletes a creature")
    void deletes_creature() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Hero");
        String creatureId = created.getId();

        assertTrue(session.deleteCreature(creatureId), "Expected deletion to succeed");
        assertFalse(session.hasCreature(creatureId), "Expected creature to be deleted");
    }

    @Test
    @DisplayName("Deletion fails for unknown creature")
    void deletion_fails_for_unknown_creature() {
        Session session = new Session();
        session.createCreature("Hero");

        assertFalse(session.deleteCreature("unknown-id"), "Expected deletion to fail");
    }

    @Test
    @DisplayName("Deleting selected creature clears selection")
    void deleting_selected_creature_clears_selection() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Hero");
        String creatureId = created.getId();
        assertEquals(creatureId, session.getSelectedCreatureId(), "Expected creature to be selected after creation");

        session.deleteCreature(creatureId);

        assertNull(session.getSelectedCreatureId(), "Expected selection to be cleared after deletion");
    }

    @Test
    @DisplayName("Deleting selected creature selects next available")
    void deleting_selected_creature_selects_next_available() {
        Session session = new Session();
        CreatureSnapshot first = session.createCreature("Hero1");
        CreatureSnapshot second = session.createCreature("Hero2");
        String secondId = second.getId();
        assertEquals(secondId, session.getSelectedCreatureId(), "Expected second creature to be selected");

        session.deleteCreature(secondId);

        assertNotNull(session.getSelectedCreatureId(), "Expected selection to be reassigned");
        assertEquals(first.getId(), session.getSelectedCreatureId(), "Expected first creature to be selected after second deleted");
    }

    @Test
    @DisplayName("Clears creature selection")
    void clears_creature_selection() {
        Session session = new Session();
        CreatureSnapshot created = session.createCreature("Hero");
        assertEquals(created.getId(), session.getSelectedCreatureId(), "Expected creature to be selected");

        session.clearSelection();

        assertNull(session.getSelectedCreatureId(), "Expected selection to be cleared");
    }

    @Test
    @DisplayName("Multiple creatures can be created and selected independently")
    void multiple_creatures_can_be_created_and_selected() {
        Session session = new Session();
        CreatureSnapshot c1 = session.createCreature("Hero1");
        CreatureSnapshot c2 = session.createCreature("Hero2");
        CreatureSnapshot c3 = session.createCreature("Hero3");

        assertEquals(c3.getId(), session.getSelectedCreatureId(), "Expected c3 to be selected after creation");

        assertTrue(session.selectCreature(c1.getId()), "Expected to select c1");
        assertEquals(c1.getId(), session.getSelectedCreatureId(), "Expected c1 to be selected");

        assertTrue(session.selectCreature(c2.getId()), "Expected to select c2");
        assertEquals(c2.getId(), session.getSelectedCreatureId(), "Expected c2 to be selected");
    }

    private void registerStandardStats() {
        for (StandardStat stat : StandardStat.values()) {
            Registries.get().stats.register(stat.getID(), stat);
        }
    }
}

