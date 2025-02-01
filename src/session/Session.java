package session;

import ability.Ability;
import ability.AbilitySet;
import ability.StandardAbility;

import java.util.HashSet;
import java.util.Set;

public class Session {

    public final SessionDefaultAbilitySet defaultAbilitySet;

    public Session () {
        Settings.STANDARD_ABILITIES.addAll(Set.of(StandardAbility.values()));
        this.defaultAbilitySet = new SessionDefaultAbilitySet();
    }

    public class Settings {
        public static int DEFAULT_ABILITY_SCORE = 10;
        public static Set<Ability> STANDARD_ABILITIES = new HashSet<>();
    }

    public class SessionDefaultAbilitySet implements AbilitySet {

        @Override
        public int getScore(Ability ability) {
            return Settings.DEFAULT_ABILITY_SCORE;
        }

        @Override
        public int getDefaultScore() {
            return Settings.DEFAULT_ABILITY_SCORE;
        }

        @Override
        public Set<Ability> getSpecified() {
            return new HashSet<>(Settings.STANDARD_ABILITIES);
        }
    }
}
