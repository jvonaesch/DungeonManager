package dungeonmanager.feature;

import dungeonmanager.stats.StatModifier;
import dungeonmanager.stats.ModifiableStatSet;

/**
 * A FeatureSection that contains a score modifier.
 */
public class StatModifierSection implements FeatureSection {

    private String id;
    private String name;
    private String description;
    private StatModifier modifier;
    private boolean visible;

    public StatModifierSection(String id, String name, String description, StatModifier modifier) {
        this(id, name, description, modifier, true);
    }

    public StatModifierSection(String id, String name, String description, StatModifier modifier, boolean visible) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.modifier = modifier;
        this.visible = visible;
    }

    public StatModifierSection(String id, String name, String description) {
        this(id, name, description, new StatModifier(), true);
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
        return "score_modifiers";
    }

    public StatModifier getModifier() {
        return modifier;
    }

    public void setModifier(StatModifier modifier) {
        this.modifier = modifier;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void onAdd(ModifiableStatSet statSet) {
        statSet.addModifier(modifier);
    }

    @Override
    public void onRemove(ModifiableStatSet statSet) {
        statSet.removeModifier(modifier);
    }

    @Override
    public String toString() {
        return "ScoreModifierSection{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", modifier=" + modifier +
                ", visible=" + visible +
                '}';
    }
}
