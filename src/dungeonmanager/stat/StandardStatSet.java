package dungeonmanager.stat;

import java.util.Set;
import java.util.TreeSet;

public class StandardStatSet implements StatSet {

    protected Set<Stat> specified;
    protected int defaultScore;
    private static StandardStatSet defaultInstance;

    public StandardStatSet(int default_score, Set<Stat> specified) {
        this.specified = specified;
        this.defaultScore = default_score;
    }

    public static StandardStatSet DEFAULT () {
        if (defaultInstance == null) {
            defaultInstance = new StandardStatSet(10, Set.of(StandardStat.values()));
        }
        return defaultInstance;
    }

    @Override
    public int getScore(Stat ability) {
        return defaultScore;
    }

    @Override
    public int getDefaultScore() {
        return defaultScore;
    }

    @Override
    public Set<Stat> getSpecified() {
        Set<Stat> specified = new TreeSet<Stat> (Stats.getDefaultComparator());
        specified.addAll(this.specified);
        return specified;
    }
}
