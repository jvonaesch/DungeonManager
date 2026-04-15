package dungeonmanager.feature;

public class Feature {

    /**
     * Can be used as a _prerequisite_ reference for other features
     */
    public final String ID;

    public String description;
    public boolean is_major_feature;
    //public final LinkedList<String> prerequisites;

    public Feature(String id, String description, boolean is_major_feature) {
        this.ID = id;
        this.description = description;
        this.is_major_feature = is_major_feature;
        //this.prerequisites = new LinkedList<>(prerequisites);
    }
}
