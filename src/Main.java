import ability.Ability;
import ability.AbilityScore;
import ability.AbilityScoreModifier;
import creature.BaseMonster;

public class Main {

    static AbilityScore strength = new AbilityScore(Ability.STR);

    public static void main(String[] args) {

        BaseMonster monster = new BaseMonster("Owlbear");
        AbilityScoreModifier modifier = new AbilityScoreModifier(monster);
        modifier.setValue(Ability.STR, 8);
        modifier.setValue(Ability.CHA, -4);
        monster.setBaseAbilityScore(Ability.STR, 16);

        System.out.println(monster);
    }
}