package dungeonmanager.stat;

import java.util.Set;

public interface StatSet {
    public int getScore(Stat ability);
    public int getDefaultScore();

    public Set<Stat> getSpecified();
}
