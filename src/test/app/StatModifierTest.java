package test.app;

import dungeonmanager.stat.DynamicStat;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.stat.Stat;
import dungeonmanager.stat.StatModifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Session Stat Modifier Tests")
public class StatModifierTest extends AppTest {

    @Test
    @DisplayName("Stat modifier correctly adds value to stat")
    void stat_modifier_adds_value() {
        StatModifier modifier = new StatModifier(StandardStat.STR.getId());
        modifier.setBaseValue(5);

        assertEquals(5, modifier.getBaseValue(), "Expected modifier to store STR +5");
    }

    @Test
    @DisplayName("Registering a custom stat")
    void stat_modifier_with_string_id_registers_custom_stat() {
        Stat fire = new DynamicStat("FIRE", "Fire", "other");
        session.registerStat(fire);

        StatModifier modifier = new StatModifier("FIRE");
        modifier.setBaseValue(3);

        Stat read_stat = session.getStat("FIRE");
        assertEquals(fire, read_stat, "Expected handle to return registered FIRE stat");
        assertEquals(3, modifier.getBaseValue(), "Expected modifier to store FIRE +3");
    }

    @Test
    @DisplayName("Multiple stat modifiers can be applied to same stat")
    void multiple_modifiers_apply_correctly() {
        StatModifier mod1 = new StatModifier(StandardStat.STR.getId());
        mod1.setBaseValue(2);

        StatModifier mod2 = new StatModifier(StandardStat.STR.getId());
        mod2.setBaseValue(3);

        assertEquals(2, mod1.getBaseValue(), "Expected first modifier to have +2");
        assertEquals(3, mod2.getBaseValue(), "Expected second modifier to have +3");
    }

    @Test
    @DisplayName("Stat modifier can apply to different stat types")
    void stat_modifier_applies_to_different_stats() {
        StatModifier modStr = new StatModifier(StandardStat.STR.getId());
        modStr.setBaseValue(4);

        StatModifier modDex = new StatModifier(StandardStat.DEX.getId());
        modDex.setBaseValue(2);

        StatModifier modCon = new StatModifier(StandardStat.CON.getId());
        modCon.setBaseValue(3);

        assertEquals(4, modStr.getBaseValue(), "Expected STR +4");
        assertEquals(2, modDex.getBaseValue(), "Expected DEX +2");
        assertEquals(3, modCon.getBaseValue(), "Expected CON +3");
    }

    @Test
    @DisplayName("Stat modifier with negative value reduces stat")
    void stat_modifier_with_negative_value_reduces_stat() {
        StatModifier modifier = new StatModifier(StandardStat.STR.getId());
        modifier.setBaseValue(-2);

        assertEquals(-2, modifier.getBaseValue(), "Expected modifier to store STR -2");
    }

    @Test
    @DisplayName("Stat modifier zero value clears stat")
    void stat_modifier_with_zero_value() {
        StatModifier modifier = new StatModifier(StandardStat.STR.getId());
        modifier.setBaseValue(5);
        modifier.setBaseValue(0);

        assertEquals(0, modifier.getBaseValue(), "Expected modifier to store STR 0");
    }
}

