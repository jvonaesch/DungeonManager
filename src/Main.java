import event.EventListener;
import event.BaseEventType;
import event.ProficiencySetModifierUpdateEvent;

import java.io.File;

public class Main {

    static final String USER_DIR = System.getProperty("user.home");
    static final String LIB_PATH = USER_DIR + "/DungeonManagerLibrary/";

    public static void main(String[] args) {

        loadLibrary();

        /*Creature monster = new Creature("Owlbear");
        AbilityScoreModifier modifier = new AbilityScoreModifier().addTo(monster);
        modifier.setValue(Ability.STR, 8);
        modifier.setValue(Ability.CHA, -4);
        monster.setBaseAbilityScore(Ability.STR, 16);

        System.out.println(monster);*/

        EventListener.addHandler(BaseEventType.PROFICIENCY_SET_MODIFIER_UPDATE, (e) -> {
            ProficiencySetModifierUpdateEvent event = (ProficiencySetModifierUpdateEvent) e;

        });
    }

    public static void loadLibrary() {
        File lib_dir = new File(LIB_PATH);
        if (!lib_dir.exists()) {
            lib_dir.mkdirs();
        }
    }
}
