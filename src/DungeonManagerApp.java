import ability.*;
import creature.Entity;
import creature.IntegratedEntityType;
import registry.Registries;
import session.Session;

import java.io.File;

public class DungeonManagerApp {

    static final String USER_DIR = System.getProperty("user.home");
    static final String LIB_PATH = USER_DIR + "/DungeonManagerLibrary/";

    private Registries registry;
    private Session session;

    public static void main(String[] args) {
        DungeonManagerApp app = new DungeonManagerApp();

        app.initialize();
    }

    public DungeonManagerApp() {
        loadLibrary();
        registry = Registries.getInstance();
    }

    public void initialize() {
        session = new Session();

        // registry.register(new DungeonManagerApp());
    }

    public static void loadLibrary() {
        File lib_dir = new File(LIB_PATH);
        if (!lib_dir.exists()) {
            lib_dir.mkdirs();
        }
    }

    public void test1() {
        Ability cosmicAwareness = new CustomAbility(
                "custom:ability:cosmic_awareness",
                "cosmic awareness",
                "COS");
        registry.register(cosmicAwareness);

        Entity monster = new Entity(IntegratedEntityType.OWLBEAR,"Bert, the Owlbear");

        monster.getAbilitySet().removeBaseScore(StandardAbility.INT);
        monster.getAbilitySet().setBaseScore(cosmicAwareness, 20);

        AbilityModifier modifier = new AbilityModifier();
        monster.addScoreModifier(modifier);

        modifier.setValue(StandardAbility.STR, 8);
        modifier.setValue(StandardAbility.CHA, 1);
        modifier.setValue(StandardAbility.INT, 4);

        System.out.println(monster);

        monster.setBaseAbilityScore(StandardAbility.INT, 5);
        System.out.println(monster);

        monster.getAbilitySet().resetBaseScore(StandardAbility.INT);
        System.out.println(monster);
    }
}
