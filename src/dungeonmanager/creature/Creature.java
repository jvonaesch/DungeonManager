package dungeonmanager.creature;

import dungeonmanager.contentpack.JsonSerializable;
import dungeonmanager.feature.Features;
import dungeonmanager.session.Session;
import dungeonmanager.stat.*;
import dungeonmanager.feature.FeatureSet;
import org.jetbrains.annotations.NotNull;

public class Creature implements CreatureBasis, JsonSerializable {

    private final String id;
    private String name;
    private CreatureBasis type;
    private final DefaultedStatSet statSet;
    private final FeatureSet feature;
    private final StatContext statContext;

    public Creature(@NotNull StatContext statContext, String id, String name, CreatureBasis type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.statSet = new DefaultedStatSet(statContext, new DefaultStatSet(statContext));
        this.feature = new FeatureSet(this.statSet);
        this.statContext = statContext;

        this.changeType(type);
    }

    public Creature(@NotNull StatContext statContext, String id, String name) {
        this(statContext, id, name, null);
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
                StatSets.toString(statSet, 1, session),
                Features.toString(feature, 1)
        );
    }

    public CreatureBasis getType() {
        return type;
    }

    public void changeType(CreatureBasis type) {
        if (type == null) this.statSet.changeParent(new DefaultStatSet(this.statContext));
        else this.statSet.changeParent(type);
        this.type = type;
    }

    public void rename(String name) {
        this.name = name;
    }

    @Override
    public ModifiableStatSet getStatSet() {
        return statSet;
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
