package dungeonmanager.registry;

import dungeonmanager.creature.Creature;
import dungeonmanager.stat.Stat;
import dungeonmanager.command.Command;
import dungeonmanager.creature.CreatureBasis;
import dungeonmanager.feature.Feature;

public class SessionRegistry {

    // public static final Registries instance = new Registries();
    // public static Registries get() { return instance; }

    public final Registry<Stat> stat;
    public final Registry<Command<?>> command;
    public final Registry<CreatureBasis> entityType;
    public final Registry<Feature> feature;
    public final Registry<Creature> creature;

    public SessionRegistry() {
        this.stat = new HashRegistry<>();
        this.command = new LazyRegistry<>();
        this.entityType = new LazyRegistry<>();
        this.feature = new LazyRegistry<>();
        this.creature = new LazyRegistry<>();
    }
}
