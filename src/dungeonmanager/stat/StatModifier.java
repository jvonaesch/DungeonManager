package dungeonmanager.stat;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single stat modification that targets one specific stat.
 * <p>
 * A StatModifier consists of:
 * - A base value that is always added to the target stat
 * - A set of dependencies (other stats) that modify the target stat's value via factors
 * </p>
 * <p>
 * Value computation: baseValue + sum(dependentStat.getValue() * factor, rounded down)
 * </p>
 *
 * @see dungeonmanager.stat.ModifiableStatSet#addModifier(dungeonmanager.stat.StatModifier) for application
 * @see dungeonmanager.stat.ModifiableStatSet#removeModifier(dungeonmanager.stat.StatModifier) for removal
 */
public class StatModifier {

    private final String targetStatId;
    private int baseValue;
    private final Map<String, Float> dependencies;

    /**
     * Create a modifier targeting a specific stat.
     *
     * @param targetStatId the ID of the stat this modifier targets
     */
    public StatModifier(@NotNull String targetStatId) {
        this.targetStatId = targetStatId;
        this.baseValue = 0;
        this.dependencies = new HashMap<>();
    }

    public String getTargetStatId() {
        return targetStatId;
    }

    public int getBaseValue() {
        return baseValue;
    }

    public StatModifier setBaseValue(int value) {
        this.baseValue = value;
        return this;
    }

    /**
     * Add or update a dependency factor for this stat modification.
     * The dependent stat's current value will be multiplied by the factor and added to the modifier.
     *
     * @param dependentStatId the ID of the stat that this stat depends on
     * @param factor          the multiplier to apply (rounded down)
     */
    public void setDependency(@NotNull String dependentStatId, float factor) {
        if (factor == 0) {
            removeDependency(dependentStatId);
            return;
        }
        dependencies.put(dependentStatId, factor);
    }

    public void removeDependency(@NotNull String dependentStatId) {
        dependencies.remove(dependentStatId);
    }

    /**
     * Get all dependency factors for this modifier.
     * Key: dependent stat ID, Value: factor
     *
     * @return a copy of the dependencies map
     */
    public Map<String, Float> getDependencies() {
        return new HashMap<>(dependencies);
    }

    public Float getDependencyFactor(@NotNull String dependentStatId) {
        return dependencies.get(dependentStatId);
    }

    @Override
    public String toString() {
        return "Modifier{" +
                "targetStat='" + targetStatId + '\'' +
                ", baseValue=" + baseValue +
                ", dependencies=" + dependencies +
                '}';
    }
}
