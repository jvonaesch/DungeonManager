package registry;

import ability.Ability;
import creature.EntityType;

public class Registries {

    private static final Registries instance = new Registries();
    public static Registries getInstance() { return instance; }

    public final LazyRegistry<Ability> abilities;
    public final LazyRegistry<EntityType> entityTypes;

    private Registries() {
        this.abilities = new LazyRegistry<Ability>();
        this.entityTypes = new LazyRegistry<EntityType>();
    }

    public void register(Object element) throws IllegalArgumentException {
        switch(element) {
            case Ability a: abilities.register(a); break;
            case EntityType t: entityTypes.register(t);
            default: throw new IllegalArgumentException("No register for type " + element.getClass().getCanonicalName());
        }
    }
}
