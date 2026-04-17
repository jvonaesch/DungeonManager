package dungeonmanager;

import dungeonmanager.feature.FeatureSection;
import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StatModifier;
import dungeonmanager.stats.CustomStat;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.creature.Creature;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.feature.ScoreModifierSection;
import dungeonmanager.registry.Registries;

import java.util.List;

public class Tests {
    static Registries registry = Registries.get();

    public static void test1() {
        Stat cosmicAwareness = new CustomStat(
                "COS", "cosmic awareness", "ability", 10
        );
        registry.stats.register(cosmicAwareness.getID(), cosmicAwareness);

        Creature monster = new Creature("Bert, the Owlbear", IntegratedCreatureType.OWLBEAR);

        monster.getStatSet().removeBaseValue(StandardStat.INT);
        monster.getStatSet().setBaseValue(cosmicAwareness, 20);

        StatModifier modifier = new StatModifier();
        monster.stats.addModifier(modifier);

        modifier.setValue(StandardStat.STR, 8)
                .setValue(StandardStat.CHA, 1)
                .setValue(StandardStat.INT, 4);

        System.out.println(monster);

        monster.stats.setBaseValue(StandardStat.INT, 5);
        System.out.println(monster);

        monster.getStatSet().resetBaseValue(StandardStat.INT);
        System.out.println(monster);
    }

    public static void test2() {
        Creature olaf = new Creature("Olaf");
        
        StatModifier modifier = new StatModifier()
                .setValue("STR", 3)
                .setValue("DEX", 1)
                .setValue(StandardStat.INT, 2);
        olaf.stats.addModifier(modifier);

        System.out.println(olaf);

        FeatureInstance appealing_feat = olaf.feature.addFeature(new Feature(
                "feat:appealing", "Appealing",
                "Increase your Charisma score by 2",
                List.of(new StatModifier[] {new StatModifier()
                        .setValue("CHA", 1)
                })
        ));
        FeatureInstance sturdy_feat = olaf.feature.addFeature(new Feature(
                "feat:sturdy", "Sturdy",
                "You are a battlefield heavyweight",
                List.of(new StatModifier[] {new StatModifier()
                        .setValue("CON", 2)
                        .setValue("MAX_HP", 10)
                })
        ));

        System.out.println(olaf);
        olaf.feature.removeFeature(appealing_feat);
        System.out.println(olaf);
    }

	public static void test3() {
		Creature warrior = new Creature("Thorgrim the Dwarf", IntegratedCreatureType.DEFAULT);

		// Create a feature with base modifiers
		Feature battleHardenedFeat = new Feature(
				"feat:battle_hardened",
				"Battle Hardened",
				"Years of combat have strengthened your resolve",
				List.of(new StatModifier().setValue(StandardStat.CON, 1))
		);

		// Add feature to creature and get the instance
		FeatureInstance instance = warrior.feature.addFeature(battleHardenedFeat);

		// Add a visible section with a single modifier that adjusts multiple stats
		ScoreModifierSection combatBonusSection = new ScoreModifierSection(
				"Combat Bonuses",
				"Additional bonuses granted by battle experience: STR +1",
				new StatModifier().setValue(StandardStat.STR, 1)
		);
		instance.addSection(combatBonusSection);

		// Add another visible section with its own modifier
		ScoreModifierSection survivalSection = new ScoreModifierSection(
				"Survival Skills",
				"Improved resilience in harsh conditions: MAX_HP +5",
				new StatModifier().setValue(StandardStat.MAX_HP, 5)
		);
		instance.addSection(survivalSection);

		// Add an invisible section with a modifier
		ScoreModifierSection internalNotesSection = new ScoreModifierSection(
				"Internal Notes",
				"[DM Only] Hidden mechanics: WIS +2",
				new StatModifier().setValue(StandardStat.WIS, 2),
				false
		);
		instance.addSection(internalNotesSection);

		System.out.println(warrior);
		System.out.println("\nFeature Sections: " + instance.getSectionCount());
		for (FeatureSection section : instance.getSections()) {
			System.out.println(" - " + section.getName() + " (visible: " + section.isVisible() + ", type: " + section.getType() + ")");
		}
	}
}
