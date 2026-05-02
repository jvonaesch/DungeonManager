package test.handle;

import dungeonmanager.session.CreatureSnapshot;
import dungeonmanager.stat.StandardStat;
import org.junit.jupiter.api.DisplayName;
import test.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Session Creature Lifecycle Tests")
public class SessionCreatureTest extends Test {

    @org.junit.jupiter.api.Test
    @DisplayName("Creates creature with default type")
    void creates_creature_with_default_type() {
        CreatureSnapshot creature = handle.createCreature("Ranger");

        assertNotNull(creature, "Expected creature to be created");
        assertEquals("Ranger", creature.getName(), "Expected creature name to match");
        assertEquals("default", creature.getSourceId(), "Expected default type");
        assertNotNull(creature.getId(), "Expected creature to have an ID");
        assertEquals(creature.getId(), handle.getSelectedCreatureId(), "Expected new creature to be auto-selected");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Creates creature with specified type")
    void creates_creature_with_specified_type() {
        CreatureSnapshot baseOwlbear = handle.createCreature("Base Owlbear", "default", new HashMap<>());
        String baseId = baseOwlbear.getId();

        CreatureSnapshot creature = handle.createCreature("Bear", baseId);

        assertNotNull(creature, "Expected creature to be created");
        assertEquals("Bear", creature.getName(), "Expected creature name to match");
        assertEquals(baseId, creature.getSourceId(), "Expected OWLBEAR type");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Creates creature with base stat overrides")
    void creates_creature_with_base_stat_overrides() {
        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 18);
        baseStats.put("DEX", 10);
        baseStats.put("CON", 16);

        CreatureSnapshot creature = handle.createCreature("Barbarian", "default", baseStats);

        assertNotNull(creature, "Expected creature to be created");
        assertEquals(18, creature.getStat("STR"), "Expected STR 18");
        assertEquals(10, creature.getStat("DEX"), "Expected DEX 10");
        assertEquals(16, creature.getStat("CON"), "Expected CON 16");
        assertEquals(Integer.valueOf(18), creature.getBaseStatOverrides().get("STR"), "Expected STR to be overridden");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Renames a creature")
    void renames_creature() {
        CreatureSnapshot created = handle.createCreature("Hero");
        String creatureId = created.getId();

        CreatureSnapshot renamed = handle.renameCreature(creatureId, "Legend");

        assertEquals("Legend", renamed.getName(), "Expected renamed creature");
        assertEquals(created.getId(), renamed.getId(), "Expected ID to remain unchanged");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Changes creature type")
    void changes_creature_type() {
        CreatureSnapshot created = handle.createCreature("Beast");

        Map<String, Integer> dwarf_stats = new HashMap<>();
        dwarf_stats.put("STR", 12);
        dwarf_stats.put("MAX_HP", 30);
        CreatureSnapshot baseDwarf = handle.createCreature("Base Dwarf", "default", dwarf_stats);

        String creatureId = created.getId();
        String basisId = baseDwarf.getId();
        CreatureSnapshot changed = handle.changeCreatureType(creatureId, basisId);

        assertEquals(basisId, changed.getSourceId(), "Expected creature type to change");
        assertEquals(created.getId(), changed.getId(), "Expected ID to remain unchanged");
        
        assertNotNull(changed.getStats(), "Expected stat map to exist");
        for (StandardStat stat : StandardStat.values()) {
            int expectedStat = dwarf_stats.getOrDefault(stat.getId(), stat.getDefaultValue());
            int actualStat = changed.getStat(stat.getId(), handle.getStatDefaults());
            assertEquals(expectedStat, actualStat, "Expected " + stat.getId() + " to match DWARF type: " + expectedStat);
        }
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Sets base stat on creature")
    void sets_base_stat() {
        CreatureSnapshot created = handle.createCreature("Warrior");
        String creatureId = created.getId();

        CreatureSnapshot edited = handle.setBaseStat(creatureId, "STR", 20);

        assertEquals(20, edited.getStat("STR"), "Expected STR to be set to 20");
        assertEquals(Integer.valueOf(20), edited.getBaseStatOverrides().get("STR"), "Expected base override to be set");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Resets base stat on creature")
    void resets_base_stat() {
        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 18);
        CreatureSnapshot created = handle.createCreature("Knight", "default", baseStats);
        String creatureId = created.getId();

        CreatureSnapshot reset = handle.resetBaseStat(creatureId, "STR");

        assertNull(reset.getBaseStatOverrides().get("STR"), "Expected base override to be reset");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Removes base stat on creature")
    void removes_base_stat() {
        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 18);
        CreatureSnapshot created = handle.createCreature("Paladin", "default", baseStats);
        String creatureId = created.getId();

        CreatureSnapshot removed = handle.removeBaseStat(creatureId, "STR");

        assertNull(removed.getBaseStatOverrides().get("STR"), "Expected base override to be removed");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Selects a creature by ID")
    void selects_creature_by_id() {
        CreatureSnapshot created = handle.createCreature("Hero");
        String creatureId = created.getId();
        handle.clearSelection();

        assertTrue(handle.selectCreature(creatureId), "Expected selection to succeed");
        assertEquals(creatureId, handle.getSelectedCreatureId(), "Expected creature to be selected");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Selection fails for unknown creature")
    void selection_fails_for_unknown_creature() {
        handle.createCreature("Hero");

        assertFalse(handle.selectCreature("unknown-id"), "Expected selection to fail");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Gets selected creature snapshot")
    void gets_selected_creature_snapshot() {
        CreatureSnapshot created = handle.createCreature("Hero");

        CreatureSnapshot selected = handle.getSelectedCreatureSnapshot();

        assertNotNull(selected, "Expected selected creature snapshot");
        assertEquals(created.getId(), selected.getId(), "Expected correct creature snapshot");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Gets selected creature snapshot when none selected")
    void gets_selected_creature_snapshot_when_none_selected() {
        handle.createCreature("Hero");
        handle.clearSelection();

        CreatureSnapshot selected = handle.getSelectedCreatureSnapshot();

        assertNull(selected, "Expected null when no creature selected");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Gets creature snapshot by ID")
    void gets_creature_snapshot_by_id() {
        CreatureSnapshot created = handle.createCreature("Hero");
        String creatureId = created.getId();
        handle.clearSelection();

        CreatureSnapshot retrieved = handle.getCreatureSnapshot(creatureId);

        assertNotNull(retrieved, "Expected creature snapshot");
        assertEquals(creatureId, retrieved.getId(), "Expected correct creature snapshot");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Checks if creature exists")
    void checks_if_creature_exists() {
        CreatureSnapshot created = handle.createCreature("Hero");
        String creatureId = created.getId();

        assertTrue(handle.hasCreature(creatureId), "Expected creature to exist");
        assertFalse(handle.hasCreature("unknown-id"), "Expected unknown creature to not exist");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Deletes a creature")
    void deletes_creature() {
        CreatureSnapshot created = handle.createCreature("Hero");
        String creatureId = created.getId();

        assertTrue(handle.deleteCreature(creatureId), "Expected deletion to succeed");
        assertFalse(handle.hasCreature(creatureId), "Expected creature to be deleted");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Deletion fails for unknown creature")
    void deletion_fails_for_unknown_creature() {
        handle.createCreature("Hero");
        assertFalse(handle.deleteCreature("unknown-id"), "Expected deletion to fail");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Deleting selected creature clears selection")
    void deleting_selected_creature_clears_selection() {
        CreatureSnapshot created = handle.createCreature("Hero");
        String creatureId = created.getId();
        assertEquals(creatureId, handle.getSelectedCreatureId(), "Expected creature to be selected after creation");

        handle.deleteCreature(creatureId);

        assertNull(handle.getSelectedCreatureId(), "Expected selection to be cleared after deletion");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Deleting selected creature clears selection when multiple creatures exist")
    void deleting_selected_creature_clears_selection_multiple() {
        CreatureSnapshot first = handle.createCreature("Hero1");
        CreatureSnapshot second = handle.createCreature("Hero2");
        String secondId = second.getId();
        assertEquals(secondId, handle.getSelectedCreatureId(), "Expected second creature to be selected");

        handle.deleteCreature(secondId);

        assertNull(handle.getSelectedCreatureId(), "Expected selection to be cleared");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Clears creature selection")
    void clears_creature_selection() {
        CreatureSnapshot created = handle.createCreature("Hero");
        assertEquals(created.getId(), handle.getSelectedCreatureId(), "Expected creature to be selected");

        handle.clearSelection();

        assertNull(handle.getSelectedCreatureId(), "Expected selection to be cleared");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Multiple creatures can be created and selected independently")
    void multiple_creatures_can_be_created_and_selected() {
        CreatureSnapshot c1 = handle.createCreature("Hero1");
        CreatureSnapshot c2 = handle.createCreature("Hero2");
        CreatureSnapshot c3 = handle.createCreature("Hero3");

        assertEquals(c3.getId(), handle.getSelectedCreatureId(), "Expected c3 to be selected after creation");

        assertTrue(handle.selectCreature(c1.getId()), "Expected to select c1");
        assertEquals(c1.getId(), handle.getSelectedCreatureId(), "Expected c1 to be selected");

        assertTrue(handle.selectCreature(c2.getId()), "Expected to select c2");
        assertEquals(c2.getId(), handle.getSelectedCreatureId(), "Expected c2 to be selected");
    }
}

