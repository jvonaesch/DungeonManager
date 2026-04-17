package test;

import dungeonmanager.creature.Creature;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.*;
import dungeonmanager.registry.Registries;
import dungeonmanager.stats.CustomStat;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StatModifier;

import java.util.Set;

public class FeatureTest {

    public static void test_stat_modifiers_1() {
        Creature olaf = new Creature("Olaf");

        StatModifier modifier = new StatModifier()
                .setValue("STR", 3)
                .setValue("DEX", 1)
                .setValue(StandardStat.INT, 2);
        olaf.stats.addModifier(modifier);

        System.out.println(olaf);

        Feature appealingFeat = new Feature("feat:appealing", "Appealing",
                "Increase your Charisma score by 2");
        appealingFeat.addSection(new StatModifierSection(
                "bonus_charisma",
                "Bonus Charisma",
                "CHA +2",
                new StatModifier().setValue("CHA", 2),
                false
        ));
        FeatureInstance appealing_feat = olaf.feature.addFeature(appealingFeat);

        Feature sturdyFeat = new Feature("feat:sturdy", "Sturdy",
                "You are a battlefield heavyweight");
        sturdyFeat.addSection(new StatModifierSection(
                "constitution_boost",
                "Constitution Boost",
                "CON +2, MAX_HP +10",
                new StatModifier().setValue("CON", 2).setValue("MAX_HP", 10),
                false
        ));
        FeatureInstance sturdy_feat = olaf.feature.addFeature(sturdyFeat);

        System.out.println(olaf);
        olaf.feature.removeFeature(appealing_feat);
        System.out.println(olaf);
    }
    public static void test_stat_modifiers_2() {
		Creature creature = new Creature("Thorgrim the Dwarf", IntegratedCreatureType.DEFAULT);

		// Create a feature with sections that add modifiers
		Feature battleHardenedFeat = new Feature(
				"feat:battle_hardened",
				"Battle Hardened",
				"Years of combat have strengthened your resolve"
		);

		// Add sections to the feature template - all instances will inherit these
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
		System.out.println("\nFeature Sections: " + instance.getSectionCount());
		for (FeatureSection section : instance.getSections()) {
			System.out.println(" - " + section.getName() + " (visible: " + section.isVisible() + ", type: " + section.getType() + ")");
		}
	}

    public static void test_selection_section() {
        Registries registry = Registries.get();

        Stat fire_resistance = new CustomStat(
                "FIRE_RESISTANCE", "Fire Resistance", "property"
        );
        registry.stats.register(fire_resistance.getID(), fire_resistance);
        Stat cold_resistance = new CustomStat(
                "COLD_RESISTANCE", "Cold Resistance", "property"
        );
        registry.stats.register(cold_resistance.getID(), cold_resistance);

        Creature creature = new Creature("Luna the Sorcerer", IntegratedCreatureType.DEFAULT);

        Feature elementalAffinityFeat = new Feature(
                "feat:elemental_affinity",
                "Elemental Affinity",
                "Choose an elemental affinity to gain related bonuses"
        );

        SelectionSection affinitySelection = new SelectionSection(
                "elemental_affinity_selection",
                "Elemental Affinity Selection",
                "Select one elemental affinity to gain its benefits",
                1)
                .addChoice("fire", new StatModifierSection(
                        "fire_affinity",
                        "Fire Affinity",
                        "Gain resistance to fire damage",
                        new StatModifier()
                                .setValue("FIRE_RESISTANCE", 1)))
                .addChoice("ice", new StatModifierSection(
                        "ice_affinity",
                        "Ice Affinity",
                        "Gain resistance to cold damage",
                        new StatModifier()
                                .setValue("COLD_RESISTANCE", 1)
        ));
        elementalAffinityFeat.addSection(affinitySelection);

        FeatureInstance instance = creature.feature.addFeature(elementalAffinityFeat);
        ((Set<String>)instance.getSelection("elemental_affinity_selection")).add("fire");
        System.out.println(instance.getSelection("elemental_affinity_selection"));

        System.out.println(creature);
        System.out.println("\nFeature Sections: " + instance.getSectionCount());
        for (FeatureSection section : instance.getSections()) {
            System.out.println(" - " + section.getName() + " (visible: " + section.isVisible() + ", type: " + section.getType() + ")");
            if (section instanceof SelectionSection) {
                System.out.println("   Choices: " + ((SelectionSection) section).getChoices().keySet());
            }
        }
    }

    public static void test_all() {
        System.out.println("=== Test Stat Modifiers 1 ===");
        test_stat_modifiers_1();
        System.out.println("\n=== Test Stat Modifiers 2 ===");
        test_stat_modifiers_2();
        System.out.println("\n=== Test Selection Section ===");
        test_selection_section();
    }
}
