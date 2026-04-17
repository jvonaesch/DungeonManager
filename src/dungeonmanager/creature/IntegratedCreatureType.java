package dungeonmanager.creature;

import dungeonmanager.stats.*;

public enum IntegratedCreatureType implements CreatureType {
    DEFAULT("default", "Default"),
    OWLBEAR("owlbear", "Owlbear", 20, 12, 17, 3, 12, 7),
    DWARF("dwarf", "Dwarf", 10, 10, 14, 10, 10, 10);

    private String id;
    private String name;
    private ModifiableStatSet abilitySet;

    private IntegratedCreatureType(String id, String name) {
        this.id = "base:dungeonmanager.creature:" + id;
        this.name = name;
        this.abilitySet = new DefaultedStatSet(DefaultStatSet.get());
    }

    private IntegratedCreatureType(String id, String name, int STR, int DEX, int CON, int INT, int WIS, int CHA) {
        this(id, name);
        this.abilitySet.setBaseValue(StandardStat.STR, STR);
        this.abilitySet.setBaseValue(StandardStat.DEX, DEX);
        this.abilitySet.setBaseValue(StandardStat.CON, CON);
        this.abilitySet.setBaseValue(StandardStat.INT, INT);
        this.abilitySet.setBaseValue(StandardStat.WIS, WIS);
        this.abilitySet.setBaseValue(StandardStat.CHA, CHA);
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
        return abilitySet;
    }
}
