package registry;

import ability.Ability;
import creature.EntityType;

public class ContentRegistry {

    private static final ContentRegistry instance = new ContentRegistry();
    public static ContentRegistry getInstance() { return instance; }

    public final Register<Ability> abilities;
    public final Register<EntityType> entityTypes;

    private ContentRegistry() {
        this.abilities = new Register<Ability> ();
        this.entityTypes = new Register<EntityType> ();
    }

    public void register(Object element) throws IllegalArgumentException {
        switch(element) {
            case Ability a: abilities.register(a); break;
            case EntityType t: entityTypes.register(t);
            default: throw new IllegalArgumentException("No register for type " + element.getClass().getCanonicalName());
        }
    }
}
