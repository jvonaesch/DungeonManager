package dungeonmanager.creature;

import dungeonmanager.stats.*;

public enum IntegratedCreatureType implements CreatureType {
    DEFAULT("base:dungeonmanager.creature:default", "default"),
    OWLBEAR("base:dungeonmanager.creature:owlbear", "owlbear", 20, 12, 17, 3, 12, 7);

    private String id;
    private String name;
    private ModifiableStatSet abilitySet;

    private IntegratedCreatureType(String id, String name) {
        this.id = id;
        this.name = name;
        this.abilitySet = new DefaultedStatSet(StandardStatSet.DEFAULT());
    }

    private IntegratedCreatureType(String id, String name, int STR, int DEX, int CON, int INT, int WIS, int CHA) {
        this(id, name);
        this.abilitySet.setBaseScore(StandardStat.STR, STR);
        this.abilitySet.setBaseScore(StandardStat.DEX, DEX);
        this.abilitySet.setBaseScore(StandardStat.CON, CON);
        this.abilitySet.setBaseScore(StandardStat.INT, INT);
        this.abilitySet.setBaseScore(StandardStat.WIS, WIS);
        this.abilitySet.setBaseScore(StandardStat.CHA, CHA);
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
