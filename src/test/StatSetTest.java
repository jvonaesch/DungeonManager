package test;

import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StatModifier;
import dungeonmanager.stats.CustomStat;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.creature.Creature;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.registry.Registries;

public class StatSetTest {
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
        monster.getStatSet().addModifier(modifier);

        modifier.setValue(StandardStat.STR, 8)
                .setValue(StandardStat.CHA, 1)
                .setValue(StandardStat.INT, 4);

        System.out.println(monster);

        monster.getStatSet().setBaseValue(StandardStat.INT, 5);
        System.out.println(monster);

        monster.getStatSet().resetBaseValue(StandardStat.INT);
        System.out.println(monster);
    }
}
