package dungeonmanager.feature;

import dungeonmanager.ability.AbilityModifier;

import java.util.ArrayList;
import java.util.Collection;

public class Feature {

    /**
     * Can be used as a _prerequisite_ reference for other features
     */
    public final String ID;

    private String description;
    private ArrayList<AbilityModifier> stat_modifiers;
    private boolean is_major_feature;
    //public final LinkedList<String> prerequisites;

    public Feature(String id, String description, Collection<AbilityModifier> stat_modifiers, boolean is_major_feature) {
        this.ID = id;
        this.description = description;
        this.is_major_feature = is_major_feature;
        this.stat_modifiers = new ArrayList<>(stat_modifiers);
        //this.prerequisites = new LinkedList<>(prerequisites);
    }

    public Feature(String id, String description, boolean is_major_feature) {
        this(id, description, new ArrayList<>(), is_major_feature);
    }

    public Feature(String id, String description, Collection<AbilityModifier> stat_modifiers) {
        this(id, description, stat_modifiers, true);
    }

    public Feature(String id, String description) {
        this(id, description, true);
    }

    public String getDescription() {
        return description;
    }

    public boolean isMajorFeature() {
        return is_major_feature;
    }

    public Collection<AbilityModifier> getStatModifiers() {
        return this.stat_modifiers;
    }
}
