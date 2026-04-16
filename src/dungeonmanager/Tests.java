package dungeonmanager;

import dungeonmanager.ability.Ability;
import dungeonmanager.ability.AbilityModifier;
import dungeonmanager.ability.CustomAbility;
import dungeonmanager.ability.StandardAbility;
import dungeonmanager.creature.Creature;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.FeatureInstance;
import dungeonmanager.registry.Registries;

import java.util.List;

public class Tests {
    static Registries registry = Registries.get();

    public static void test1() {
        Ability cosmicAwareness = new CustomAbility(
                "COS", "cosmic awareness", "custom:dungeonmanager.ability:cosmic_awareness"
        );
        registry.ability.register(cosmicAwareness.getID(), cosmicAwareness);

        Creature monster = new Creature("Bert, the Owlbear", IntegratedCreatureType.OWLBEAR);

        monster.getAbilitySet().removeBaseScore(StandardAbility.INT);
        monster.getAbilitySet().setBaseScore(cosmicAwareness, 20);

        AbilityModifier modifier = new AbilityModifier();
        monster.ability.addModifier(modifier);

        modifier.setValue(StandardAbility.STR, 8)
                .setValue(StandardAbility.CHA, 1)
                .setValue(StandardAbility.INT, 4);

        System.out.println(monster);

        monster.ability.setBaseScore(StandardAbility.INT, 5);
        System.out.println(monster);

        monster.getAbilitySet().resetBaseScore(StandardAbility.INT);
        System.out.println(monster);
    }

    public static void test2() {
        Creature olaf = new Creature("Olaf");
        
        AbilityModifier modifier = new AbilityModifier()
                .setValue("STR", 3)
                .setValue("DEX", 1)
                .setValue(StandardAbility.INT, 2);
        olaf.ability.addModifier(modifier);

        System.out.println(olaf);
        System.out.println(olaf.feature.getAllFeatures());

        FeatureInstance feat = olaf.feature.addFeature(
                "feat:appealing",
                new Feature(
                        "feat:appealing",
                        "Increase your Charisma score by 2",
                        List.of(new AbilityModifier[] {new AbilityModifier()
                                .setValue("CHA", 1)
                        })
                )
        );

        System.out.println(olaf);
        System.out.println(olaf.feature.getAllFeatures());

        olaf.feature.removeFeature("feat:appealing");

        System.out.println(olaf);
        System.out.println(olaf.feature.getAllFeatures());
    }
}
