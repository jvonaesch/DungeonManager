package dungeonmanager.registry;

import dungeonmanager.stat.IStat;
import dungeonmanager.command.Command;
import dungeonmanager.creature.CreatureBasis;
import dungeonmanager.feature.Feature;

public class Registries {

    // public static final Registries instance = new Registries();
    // public static Registries get() { return instance; }

    public final Registry<IStat> stat;
    public final Registry<Command<?>> command;
    public final Registry<CreatureBasis> entityType;
    public final Registry<Feature> feature;

    public Registries() {
        this.stat = new HashRegistry<>();
        this.command = new LazyRegistry<>();
        this.entityType = new LazyRegistry<>();
        this.feature = new LazyRegistry<>();
    }
}
