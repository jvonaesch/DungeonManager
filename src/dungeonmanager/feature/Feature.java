package dungeonmanager.feature;

public class Feature {

    public String description;
    public boolean is_major_feature;
    //public final LinkedList<String> prerequisites;

    public Feature(String description, boolean is_major_feature) {
        this.description = description;
        this.is_major_feature = is_major_feature;
        //this.prerequisites = new LinkedList<>(prerequisites);
    }
}
