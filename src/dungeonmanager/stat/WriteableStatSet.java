package dungeonmanager.stat;

public interface WriteableStatSet extends StatSet {

    void setBaseValue(String statId, Integer value);
    default void setBaseValue(IStat stat, Integer value) {
        setBaseValue(stat.getId(), value);
    }

    void resetBaseValue(IStat stat);

    void removeBaseValue(String statId);
    default void removeBaseValue(IStat stat) {
        removeBaseValue(stat.getId());
    }
}
