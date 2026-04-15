package dungeonmanager.registry;

import dungeonmanager.ability.Ability;
import dungeonmanager.command.Command;
import dungeonmanager.creature.CreatureType;
import dungeonmanager.feature.Feature;

public class Registries {

    private static final Registries instance = new Registries();
    public static Registries get() { return instance; }

    public final LazyRegistry<Ability> ability;
    public final LazyRegistry<Command> command;
    public final LazyRegistry<CreatureType> entityType;
    public final LazyRegistry<Feature> feature;

    private Registries() {
        this.ability = new LazyRegistry<Ability>();
        this.command = new LazyRegistry<Command>();
        this.entityType = new LazyRegistry<CreatureType>();
        this.feature = new LazyRegistry<Feature>();
    }
}
