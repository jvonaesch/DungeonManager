package dungeonmanager;

import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StatModifier;
import dungeonmanager.stats.CustomStat;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.creature.Creature;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.registry.Registries;

import java.util.List;

public class Tests {
    static Registries registry = Registries.get();

    public static void test1() {
        Stat cosmicAwareness = new CustomStat(
                "COS", "cosmic awareness", "custom:dungeonmanager.stats:cosmic_awareness"
        );
        registry.ability.register(cosmicAwareness.getID(), cosmicAwareness);

        Creature monster = new Creature("Bert, the Owlbear", IntegratedCreatureType.OWLBEAR);

        monster.getStatSet().removeBaseScore(StandardStat.INT);
        monster.getStatSet().setBaseScore(cosmicAwareness, 20);

        StatModifier modifier = new StatModifier();
        monster.stats.addModifier(modifier);

        modifier.setValue(StandardStat.STR, 8)
                .setValue(StandardStat.CHA, 1)
                .setValue(StandardStat.INT, 4);

        System.out.println(monster);

        monster.stats.setBaseScore(StandardStat.INT, 5);
        System.out.println(monster);

        monster.getStatSet().resetBaseScore(StandardStat.INT);
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
        System.out.println(olaf.feature.getAllFeatures());

        FeatureInstance appealing_feat = olaf.feature.addFeature(
                "feat:appealing",
                new Feature(
                        "feat:appealing",
                        "Increase your Charisma score by 2",
                        List.of(new StatModifier[] {new StatModifier()
                                .setValue("CHA", 1)
                        })
                )
        );
        FeatureInstance sturdy_feat = olaf.feature.addFeature(
                "feat:sturdy",
                new Feature(
                        "feat:sturdy",
                        "You are a battlefield heavyweight",
                        List.of(new StatModifier[] {new StatModifier()
                                .setValue("CON", 2)
                                //.setValue("HP", 10)
                        })
                )
        );

        System.out.println(olaf);
        System.out.println(olaf.feature.getAllFeatures());

        olaf.feature.removeFeature(appealing_feat);

        System.out.println(olaf);
        System.out.println(olaf.feature.getAllFeatures());
    }
}
