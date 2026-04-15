package dungeonmanager;

import dungeonmanager.ability.Ability;
import dungeonmanager.ability.AbilityModifier;
import dungeonmanager.ability.CustomAbility;
import dungeonmanager.ability.StandardAbility;
import dungeonmanager.creature.Creature;
import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.registry.Registries;

public class Tests {
    static Registries registry = Registries.get();

    public static void test1() {
        Ability cosmicAwareness = new CustomAbility(
                "custom:dungeonmanager.ability:cosmic_awareness",
                "cosmic awareness",
                "COS");
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
        
        AbilityModifier modifier = new AbilityModifier();
        modifier.setValue(StandardAbility.STR, 3)
                .setValue(StandardAbility.CHA, 1)
                .setValue(StandardAbility.INT, 4);

        olaf.ability.addModifier(modifier);
        System.out.println(olaf);
    }
}
