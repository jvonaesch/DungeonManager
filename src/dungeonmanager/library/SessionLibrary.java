package dungeonmanager.library;

import dungeonmanager.creature.Creature;
import dungeonmanager.command.Command;
import dungeonmanager.creature.CreatureBasis;
import dungeonmanager.feature.ModifyingFeature;

public class SessionLibrary {

    // public static final Registries instance = new Registries();
    // public static Registries get() { return instance; }

    public final StatLibrary stat;
    public final Library<Command<?>> command;
    public final Library<CreatureBasis> entityType;
    public final Library<ModifyingFeature> feature;
    public final Library<Creature> creature;

    public SessionLibrary() {
        this.stat = new StatLibrary();
        this.command = new LazyLibrary<>();
        this.entityType = new LazyLibrary<>();
        this.feature = new LazyLibrary<>();
        this.creature = new LazyLibrary<>();
    }
}
