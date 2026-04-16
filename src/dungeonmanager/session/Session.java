package dungeonmanager.session;

import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StatSet;
import dungeonmanager.stats.StandardStat;

import java.util.HashSet;
import java.util.Set;

public class Session {

    public final SessionDefaultStatSet defaultAbilitySet;

    public Session () {
        Settings.STANDARD_ABILITIES.addAll(Set.of(StandardStat.values()));
        this.defaultAbilitySet = new SessionDefaultStatSet();
    }

    public class Settings {
        public static int DEFAULT_ABILITY_SCORE = 10;
        public static Set<Stat> STANDARD_ABILITIES = new HashSet<>();
    }

    public class SessionDefaultStatSet implements StatSet {

        @Override
        public int getScore(Stat stat) {
            return Settings.DEFAULT_ABILITY_SCORE;
        }

        @Override
        public int getDefaultScore() {
            return Settings.DEFAULT_ABILITY_SCORE;
        }

        @Override
        public Set<Stat> getSpecified() {
            return new HashSet<>(Settings.STANDARD_ABILITIES);
        }
    }
}
