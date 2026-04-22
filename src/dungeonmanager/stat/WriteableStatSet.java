package dungeonmanager.stat;

public interface WriteableStatSet extends StatSet {

    void setBaseValue(Stat stat, Integer value);
    void resetBaseValue(Stat stat);
    void removeBaseValue(Stat stat);

}
