package dungeonmanager.creature;

import dungeonmanager.stat.StatSet;

public class CreatureType implements CreatureBasis {

    private final String id;
    private final String name;
    private final StatSet statSet;

    public CreatureType(String id, String name, StatSet statSet) {
        this.id = id;
        this.name = name;
        this.statSet = statSet;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public StatSet getStatSet() {
        return statSet;
    }
}
