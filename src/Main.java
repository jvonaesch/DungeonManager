import ability.Ability;
import ability.AbilityScore;
import ability.AbilityScoreModifier;

public class Main {

    static AbilityScore strength = new AbilityScore(Ability.STR);

    public static void main(String[] args) {

        AbilityScoreModifier mod = new AbilityScoreModifier(Ability.STR, 3);

        System.out.println(strength);
        strength.addScoreModifier(mod);
        System.out.println(strength);
    }
}