package dungeonmanager.registry;

import dungeonmanager.ability.Ability;
import dungeonmanager.command.Command;
import dungeonmanager.creature.EntityType;

public class Registries {

    private static final Registries instance = new Registries();
    public static Registries getInstance() { return instance; }

    public final LazyRegistry<Ability> ability;
    public final LazyRegistry<Command> command;
    public final LazyRegistry<EntityType> entityType;

    private Registries() {
        this.ability = new LazyRegistry<Ability>();
        this.command = new LazyRegistry<Command>();
        this.entityType = new LazyRegistry<EntityType>();
    }

    /*public void register(Object element) throws IllegalArgumentException {
        switch(element) {
            case Ability a: abilities.register(a); break;
            case EntityType t: entityTypes.register(t);
            default: throw new IllegalArgumentException("No register for type " + element.getClass().getCanonicalName());
        }
    }*/
}
