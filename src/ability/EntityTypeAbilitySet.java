package ability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Deprecated
public class EntityTypeAbilitySet extends BaseAbilitySet {

    public EntityTypeAbilitySet(Map<Ability, Integer> base_abilities) {
        this.modifiers = new HashSet<AbilityModifier>();
        this.base_scores = new HashMap<Ability, Integer>(base_abilities);
        this.scores = new HashMap<Ability, Integer> ();
    }

    public EntityTypeAbilitySet() {
        this(new HashMap<Ability, Integer> ());
    }

    public EntityTypeAbilitySet(Set<Ability> base_abilities, Integer base_value) {
        this();
        for (Ability ability: base_abilities) this.base_scores.put(ability, base_value.intValue());
    }

    public EntityTypeAbilitySet(Set<Ability> base_abilities) {
        this(base_abilities, 10);
    }
}
