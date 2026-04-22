package dungeonmanager.creature;

import dungeonmanager.stat.*;

public enum IntegratedCreatureType implements CreatureType {
    DEFAULT("default", "Default"),
    OWLBEAR("owlbear", "Owlbear", 20, 12, 17, 3, 12, 7),
    DWARF("dwarf", "Dwarf", 10, 10, 14, 10, 10, 10);

    private final String id;
    private final String name;
    private final WriteableStatSet statSet;

    IntegratedCreatureType(String id, String name) {
        this.id = "base:dungeonmanager.creature:" + id;
        this.name = name;
        this.statSet = new DefaultedStatSet(DefaultStatSet.get());
    }

    IntegratedCreatureType(String id, String name, int STR, int DEX, int CON, int INT, int WIS, int CHA) {
        this(id, name);
        this.statSet.setBaseValue(StandardStat.STR, STR);
        this.statSet.setBaseValue(StandardStat.DEX, DEX);
        this.statSet.setBaseValue(StandardStat.CON, CON);
        this.statSet.setBaseValue(StandardStat.INT, INT);
        this.statSet.setBaseValue(StandardStat.WIS, WIS);
        this.statSet.setBaseValue(StandardStat.CHA, CHA);
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public StatSet getStatSet() {
        return statSet;
    }
}
