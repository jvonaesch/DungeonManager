import ability.*;
import creature.Entity;
import creature.IntegratedEntityType;
import session.Session;

import java.io.File;

public class Main {

    static final String USER_DIR = System.getProperty("user.home");
    static final String LIB_PATH = USER_DIR + "/DungeonManagerLibrary/";

    public static void main(String[] args) {

        loadLibrary();

        Session session = new Session();
        Ability cosmicAwareness = new CustomAbility(
                "custom:ability:cosmic_awareness",
                "cosmic awareness",
                "COS");

        Entity monster = new Entity(IntegratedEntityType.OWLBEAR,"Bert, the Owlbear");
        monster.getAbilitySet().removeBaseScore(StandardAbility.INT);
        monster.getAbilitySet().setBaseScore(cosmicAwareness, 20);

        AbilityModifier modifier = new AbilityModifier();
        monster.addScoreModifier(modifier);

        modifier.setValue(StandardAbility.STR, 8);
        modifier.setValue(StandardAbility.CHA, -4);
        modifier.setValue(StandardAbility.INT, -6);
        monster.setBaseAbilityScore(StandardAbility.STR, 16);

        System.out.println(monster);

        monster.getAbilitySet().resetBaseScore(StandardAbility.INT);

        System.out.println(monster);
     }

    public static void loadLibrary() {
        File lib_dir = new File(LIB_PATH);
        if (!lib_dir.exists()) {
            lib_dir.mkdirs();
        }
    }
}
