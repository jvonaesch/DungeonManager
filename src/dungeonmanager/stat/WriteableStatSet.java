package dungeonmanager.stat;

public interface WriteableStatSet extends StatSet {

    void setBaseValue(String statId, Integer value);
    default void setBaseValue(Stat stat, Integer value) {
        setBaseValue(stat.getId(), value);
    };

    void resetBaseValue(Stat stat);

    void removeBaseValue(String statId);
    default void removeBaseValue(Stat stat) {
        removeBaseValue(stat.getId());
    };
}
