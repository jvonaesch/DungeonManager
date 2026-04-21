package test.session;

import dungeonmanager.registry.Registries;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.stats.StatModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Session Stat Modifier Tests")
public class SessionStatModifierTest {

    @BeforeEach
    void setUp() {
        registerStandardStats();
    }

    @Test
    @DisplayName("Stat modifier correctly adds value to stat")
    void stat_modifier_adds_value() {
        StatModifier modifier = new StatModifier();
        modifier.setValue(StandardStat.STR, 5);

        assertEquals(5, modifier.getValue(StandardStat.STR), "Expected modifier to store STR +5");
    }

    @Test
    @DisplayName("Stat modifier with string stat ID creates and registers custom stat")
    void stat_modifier_with_string_id_registers_custom_stat() {
        StatModifier modifier = new StatModifier();
        modifier.setValue("FIRE", 3);

        assertEquals(3, modifier.getValue("FIRE"), "Expected modifier to store FIRE +3");
        assertNotNull(Registries.get().stats.get("FIRE"), "Expected FIRE custom stat to be registered");
    }

    @Test
    @DisplayName("Multiple stat modifiers can be applied to same stat")
    void multiple_modifiers_apply_correctly() {
        StatModifier mod1 = new StatModifier();
        mod1.setValue(StandardStat.STR, 2);

        StatModifier mod2 = new StatModifier();
        mod2.setValue(StandardStat.STR, 3);

        assertEquals(2, mod1.getValue(StandardStat.STR), "Expected first modifier to have +2");
        assertEquals(3, mod2.getValue(StandardStat.STR), "Expected second modifier to have +3");
    }

    @Test
    @DisplayName("Stat modifier can apply to different stat types")
    void stat_modifier_applies_to_different_stats() {
        StatModifier modifier = new StatModifier();
        modifier.setValue(StandardStat.STR, 4);
        modifier.setValue(StandardStat.DEX, 2);
        modifier.setValue(StandardStat.CON, 3);

        assertEquals(4, modifier.getValue(StandardStat.STR), "Expected STR +4");
        assertEquals(2, modifier.getValue(StandardStat.DEX), "Expected DEX +2");
        assertEquals(3, modifier.getValue(StandardStat.CON), "Expected CON +3");
    }

    @Test
    @DisplayName("Stat modifier with negative value reduces stat")
    void stat_modifier_with_negative_value_reduces_stat() {
        StatModifier modifier = new StatModifier();
        modifier.setValue(StandardStat.STR, -2);

        assertEquals(-2, modifier.getValue(StandardStat.STR), "Expected modifier to store STR -2");
    }

    @Test
    @DisplayName("Stat modifier zero value clears stat")
    void stat_modifier_with_zero_value() {
        StatModifier modifier = new StatModifier();
        modifier.setValue(StandardStat.STR, 5);
        modifier.setValue(StandardStat.STR, 0);

        assertEquals(0, modifier.getValue(StandardStat.STR), "Expected modifier to store STR 0");
    }

    private void registerStandardStats() {
        for (StandardStat stat : StandardStat.values()) {
            Registries.get().stats.register(stat.getID(), stat);
        }
    }
}

