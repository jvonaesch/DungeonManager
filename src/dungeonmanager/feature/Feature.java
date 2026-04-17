package dungeonmanager.feature;

import java.util.ArrayList;
import java.util.List;

public class Feature {

    /**
     * Can be used as a _prerequisite_ reference for other features
     */
    public final String ID;
    private String name;
    private String description;
    private boolean is_major_feature;
    private List<FeatureSection> sections;
    //public final LinkedList<String> prerequisites;

    public Feature(String id, String name, String description, boolean is_major_feature) {
        this.ID = id;
        this.description = description;
        this.is_major_feature = is_major_feature;
        this.name = name;
        this.sections = new ArrayList<>();
        //this.prerequisites = new LinkedList<>(prerequisites);
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

    public Feature addSection(FeatureSection section) {
        if (section != null) {
            sections.add(section);
        }
        return this;
    }

    public List<FeatureSection> getSections() {
        return new ArrayList<>(sections);
    }

    public int getSectionCount() {
        return sections.size();
    }
}
