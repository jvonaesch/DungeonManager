package dungeonmanager.creature;

import dungeonmanager.contentpack.JsonSerializable;
import dungeonmanager.feature.Features;
import dungeonmanager.session.CreatureSnapshot;
import dungeonmanager.session.Session;
import dungeonmanager.stat.*;
import dungeonmanager.feature.FeatureSet;

public class Creature implements CreatureBasis, JsonSerializable {

    private final String id;
    private String name;
    private CreatureBasis type;
    private final DefaultedStatSet stats;
    private final FeatureSet feature;

    public Creature(String id, String name, CreatureBasis type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.stats = new DefaultedStatSet(this.type);
        this.feature = new FeatureSet(this.stats);
    }

    public Creature(String id, String name) {
        this(id, name, IntegratedCreatureType.DEFAULT);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getID() {
        return id;
    }

    public String toString(Session session) {
        return "\n\"%s\":\n %s%s".formatted(
                name,
                StatSets.toString(stats, 1, session),
                Features.toString(feature, 1)
        );
    }

    public CreatureBasis getType() {
        return type;
    }

    public void changeType(CreatureBasis type) {
        this.type = type;
        this.stats.changeParent(type);
    }

    public void rename(String name) {
        this.name = name;
    }

    @Override
    public ModifiableStatSet getStatSet() {
        return stats;
    }

    public FeatureSet getFeatureSet() {
        return feature;
    }

    @Override
    public String toJson() {
        return "";
    }

    public static Creature fromJson(String creatureId, String json) {
        return null;
    }
}
