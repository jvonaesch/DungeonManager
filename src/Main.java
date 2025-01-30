import ability.AbilityModifier;
import ability.StandardAbility;
import creature.Entity;
import creature.IntegratedEntityType;

import java.io.File;

public class Main {

    static final String USER_DIR = System.getProperty("user.home");
    static final String LIB_PATH = USER_DIR + "/DungeonManagerLibrary/";

    public static void main(String[] args) {

        loadLibrary();

        Entity monster = new Entity(IntegratedEntityType.OWLBEAR,"Bert, the Owlbear");
        AbilityModifier modifier = new AbilityModifier();

        monster.addScoreModifier(modifier);

        modifier.setValue(StandardAbility.STR, 8);
        modifier.setValue(StandardAbility.CHA, -4);
        monster.setBaseAbilityScore(StandardAbility.STR, 16);

        System.out.println(monster);
    }

    public static void loadLibrary() {
        File lib_dir = new File(LIB_PATH);
        if (!lib_dir.exists()) {
            lib_dir.mkdirs();
        }
    }
}
