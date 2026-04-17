package dungeonmanager.feature;

import dungeonmanager.stats.StatModifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Feature {

    /**
     * Can be used as a _prerequisite_ reference for other features
     */
    public final String ID;
    private String name;
    private String description;
    private ArrayList<StatModifier> stat_modifiers;
    private boolean is_major_feature;
    private List<FeatureSection> sections;
    //public final LinkedList<String> prerequisites;

    public Feature(String id, String name, String description, Collection<StatModifier> stat_modifiers, boolean is_major_feature) {
        this.ID = id;
        this.description = description;
        this.is_major_feature = is_major_feature;
        this.stat_modifiers = new ArrayList<>(stat_modifiers);
        this.name = name;
        this.sections = new ArrayList<>();
        //this.prerequisites = new LinkedList<>(prerequisites);
    }

    public Feature(String id, String name, String description, boolean is_major_feature) {
        this(id, name, description, new ArrayList<>(), is_major_feature);
    }

    public Feature(String id, String name, String description, Collection<StatModifier> stat_modifiers) {
        this(id, name, description, stat_modifiers, true);
    }

    public Feature(String id, String name, String description) {
        this(id, name, description, true);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return this.name;
    }

    public boolean isMajorFeature() {
        return is_major_feature;
    }

    public Collection<StatModifier> getStatModifiers() {
        return this.stat_modifiers;
    }

    public void addSection(FeatureSection section) {
        if (section != null) {
            sections.add(section);
        }
    }

    public List<FeatureSection> getSections() {
        return new ArrayList<>(sections);
    }

    public int getSectionCount() {
        return sections.size();
    }
}
