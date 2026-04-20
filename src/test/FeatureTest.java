package test;

import dungeonmanager.creature.Creature;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.*;
import dungeonmanager.registry.Registries;
import dungeonmanager.stats.CustomStat;
import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.stats.StatModifier;

import java.util.Set;


@SuppressWarnings("unused")
public class FeatureTest {

    public static void test_stat_modifiers_1() {
        Creature olaf = new Creature("Olaf");

        System.out.println(olaf);

        Feature appealing_feat = new Feature("feat:appealing", "Appealing",
                "Increase your Charisma score by 2")
                .addSection(new StatModifierSection(
                        "bonus_charisma",
                        "Bonus Charisma",
                        "CHA +2",
                        new StatModifier()
                                .setValue("CHA", 2),
                        false
                ));
        FeatureInstance appealing_instance = olaf.feature.addFeature(appealing_feat);

        Feature sturdy_feat = new Feature("feat:sturdy", "Sturdy",
                "You are a battlefield heavyweight")
                .addSection(new StatModifierSection(
                        "constitution_boost",
                        "Constitution Boost",
                        "CON +2, MAX_HP +10",
                        new StatModifier()
                                .setValue("CON", 2)
                                .setValue("MAX_HP", 10),
                        false
                ));

        olaf.feature.addFeature(sturdy_feat);
        System.out.println(olaf);

        olaf.feature.removeFeature(appealing_instance);
        System.out.println(olaf);
    }
    public static void test_stat_modifiers_2() {
		Creature creature = new Creature("Thorgrim the Dwarf", IntegratedCreatureType.DEFAULT);

		Feature battleHardenedFeat = new Feature(
				"feat:battle_hardened",
				"Battle Hardened",
				"Years of combat have strengthened your resolve"
		);

		StatModifierSection combatBonusSection = new StatModifierSection(
				"combat_bonuses",
				"Combat Bonuses",
				"Additional bonuses granted by battle experience: STR +1",
				new StatModifier().setValue(StandardStat.STR, 1)
		);
		battleHardenedFeat.addSection(combatBonusSection);

		StatModifierSection survivalSection = new StatModifierSection(
				"survival_skills",
				"Survival Skills",
				"Improved resilience in harsh conditions: MAX_HP +5",
				new StatModifier().setValue(StandardStat.MAX_HP, 5)
		);
		battleHardenedFeat.addSection(survivalSection);

		FeatureInstance instance = creature.feature.addFeature(battleHardenedFeat);
		System.out.println(creature);

        battleHardenedFeat.removeSection(combatBonusSection);
        instance.reload();
        System.out.println(creature);
	}
    public static void test_stat_modifiers_3() {
        String unknownStatID = "ARC";
        Creature creature = new Creature("Mira the Sage", IntegratedCreatureType.DEFAULT);

        Feature improviserFeat = new Feature(
                "feat:improvised_study",
                "Improvised Study",
                "A practical breakthrough grants an unusual bonus stat."
        );
        improviserFeat.addSection(new StatModifierSection(
                "arcane_reserve_bonus",
                "Arcane Reserve Bonus",
                "ARC +1",
                new StatModifier().setValue(unknownStatID, 1)
        ));

        creature.feature.addFeature(improviserFeat);

        Stat registeredStat = Registries.get().stats.get(unknownStatID);
        if (registeredStat == null) {
            throw new IllegalStateException("Expected stat to be registered: " + unknownStatID);
        }
        if (!(registeredStat instanceof CustomStat)) {
            throw new IllegalStateException("Expected " + unknownStatID + " to be a CustomStat");
        }
        if (!"other".equals(registeredStat.getType())) {
            throw new IllegalStateException("Expected type 'other' for " + unknownStatID + " but got " + registeredStat.getType());
        }
        if (creature.getStatSet().getValue(unknownStatID) != 1) {
            throw new IllegalStateException("Expected " + unknownStatID + " value 1 but got " + creature.getStatSet().getValue(unknownStatID));
        }

        System.out.println(creature);
        System.out.println("Registered custom stat: " + registeredStat.getID() + " (type=" + registeredStat.getType() + ")");
    }

    @SuppressWarnings("unchecked")
    public static void test_sections_1() {
        Creature creature = new Creature("Wilbur the Wizard", IntegratedCreatureType.DEFAULT);

        Feature feat = new Feature(
                "feat:experienced_caster",
                "Experienced Caster",
                "You've spent years casting spells. The practice shows."
        );

        SelectionSection selection = new SelectionSection(
                "spellcast_selection",
                "Spellcasting Ability Selection",
                "Select one of the common spellcasting abilities to get a +1 bonus in it",
                1)
                .addOption(new StatModifierSection(
                        "charisma",
                        "Charisma Bonus",
                        "Gain a permanent +1 in CHA",
                        new StatModifier()
                                .setValue("CHA", 1)))
                .addOption(new StatModifierSection(
                        "intelligence",
                        "Intelligence Bonus",
                        "Gain a permanent +1 in INT",
                        new StatModifier()
                                .setValue("INT", 1)))
                .addOption(new StatModifierSection(
                        "wisdom",
                        "Wisdom Bonus",
                        "Gain a permanent +1 in WIS",
                        new StatModifier()
                                .setValue("WIS", 1))
                );
        feat.addSection(selection);

        FeatureInstance instance = creature.feature.addFeature(feat);
        ((Set<String>)instance.getSelection("spellcast_selection")).add("intelligence");
        instance.reload();
        System.out.println(selection.getConfiguration());
        System.out.println(instance.getSelection("elemental_affinity_selection"));

        System.out.println(creature);
        System.out.println("\nFeature Sections: " + instance.getSectionCount());
        for (FeatureSection section : instance.getSections()) {
            System.out.println(" - " + section.getName() + " (visible: " + section.isVisible() + ", type: " + section.getType() + ")");
            if (section instanceof SelectionSection) {
                System.out.println("   Choices: " + ((SelectionSection) section).getConfiguration().keySet());
            }
        }
    }

    public static void test_modifiers() {
        System.out.println("=== Test Stat Modifiers 1 ===");
        test_stat_modifiers_1();
        System.out.println("\n=== Test Stat Modifiers 2 ===");
        test_stat_modifiers_2();
         System.out.println("\n=== Test Stat Modifiers 3 ===");
        test_stat_modifiers_3();
    }
    public static void test_sections() {
        System.out.println("=== Test Selection Section ===");
        test_sections_1();
    }
    public static void test_all() {
        test_modifiers();
        test_sections();
    }
}
