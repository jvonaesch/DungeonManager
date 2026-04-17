package dungeonmanager.feature;

import dungeonmanager.stats.ModifiableStatSet;

/**
 * A section within a Feature that groups related information together.
 * Sections can contain different types of content (e.g., score modifiers, descriptions).
 * Sections can be marked as visible to control whether they appear in the feature's display.
 */
public interface FeatureSection {

    /**
     * @return the unique identifier for this section
     */
    String getID();

    /**
     * @return the title/name of this section
     */
    String getName();

    /**
     * @return the description of this section
     */
    String getDescription();

    /**
     * @return whether this section should be displayed in the feature's UI
     */
    boolean isVisible();

    /**
     * @return the type identifier for this section (e.g., "score_modifiers")
     */
    String getType();

    /**
     * Called when this section is added to a FeatureInstance.
     * Implementations should perform any necessary setup (e.g., registering modifiers).
     * @param statSet the stat set of the creature this section is being added to
     */
    default void onAdd(ModifiableStatSet statSet) {
        // Default implementation does nothing
    }

    /**
     * Called when this section is removed from a FeatureInstance.
     * Implementations should perform any necessary cleanup (e.g., unregistering modifiers).
     * @param statSet the stat set of the creature this section is being removed from
     */
    default void onRemove(ModifiableStatSet statSet) {
        // Default implementation does nothing
    }

    /**
     * Called when this section is loaded into a FeatureInstance.
     * Implementations should add themselves and any child sections to the instance.
     * @param instance the FeatureInstance to load into
     */
    default void loadToInstance(FeatureInstance instance) {
        instance.addSection(this);
    }
}
