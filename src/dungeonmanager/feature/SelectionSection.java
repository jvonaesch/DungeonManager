package dungeonmanager.feature;

import java.util.*;

/**
 * A FeatureSection that allows choosing from multiple subsections.
 * A specified number of subsections can be selected and added to the FeatureInstance.
 */
public class SelectionSection implements ConfiguredFeatureSection {

    private String id;
    private String name;
    private String description;
    private boolean visible;
    private int numSelections;
    private Map<String, FeatureSection> configuration;

    public SelectionSection(String id, String name, String description, int numSelections) {
        this(id, name, description, numSelections, true);
    }

    public SelectionSection(String id, String name, String description, int numSelections, boolean visible) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.numSelections = numSelections;
        this.visible = visible;
        this.configuration = new HashMap<>();
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public String getType() {
        return "selection";
    }

    /**
     * Adds a choice option to this selection section.
     * @param subsection the subsection to add as a choice
     * @return this SelectionSection for chaining
     */
    public SelectionSection addOption(FeatureSection subsection) {
        if (subsection != null) {
            configuration.put(subsection.getID(), subsection);
        }
        return this;
    }

    /**
     * @return the number of selections required from this section
     */
    public int getNumSelections() {
        return numSelections;
    }

    /**
     * @return map of choice section ID to subsection
     */
    public Map<String, FeatureSection> getConfiguration() {
        return new HashMap<>(configuration);
    }

    /**
     * Generates a compound ID from this section's ID and a choice ID.
     * @param choiceID the selected choice
     * @return compound ID suitable for tracking the selection
     */
    public String getOptionID(String choiceID) {
        return id + ":" + choiceID;
    }

    @Override
    public String toString() {
        return "SelectionSection{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", numSelections=" + numSelections +
                ", choices=" + configuration.keySet() +
                ", visible=" + visible +
                '}';
    }

    @Override
    public void loadToInstance(FeatureInstance instance) {
        instance.addSection(this);
        Object selection_object = instance.getSelection(this.id);
        Set<String> selection;
        if (selection_object instanceof Set) selection = (Set<String>) selection_object;
        else {
            selection = new TreeSet<>();
            instance.setSelection(this.id, selection);
        }

        for (String key : selection) {
            configuration.get(key).loadToInstance(instance);
        }
    }

    @Override
    public Class getConfigType() {
        return SelectionConfig.class;
    }

    public static class SelectionConfig extends TreeSet<String> {}
}
