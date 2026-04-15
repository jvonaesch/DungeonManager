package dungeonmanager.registry;

import dungeonmanager.ability.Ability;
import dungeonmanager.command.Command;
import dungeonmanager.creature.CreatureType;
import dungeonmanager.feature.Feature;

public class Registries {

    private static final Registries instance = new Registries();
    public static Registries get() { return instance; }

    public final Registry<Ability> ability;
    public final Registry<Command<?>> command;
    public final Registry<CreatureType> entityType;
    public final Registry<Feature> feature;

    private Registries() {
        this.ability = new TreeRegistry<>();
        this.command = new LazyRegistry<>();
        this.entityType = new LazyRegistry<>();
        this.feature = new LazyRegistry<>();
    }
}
