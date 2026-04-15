package dungeonmanager.ability;

import java.util.*;

public class BaseAbilitySet implements ModifiableAbilitySet {

    public Set<AbilityModifier> modifiers;
    public Map<Ability, Integer> base_scores;
    public Map<Ability, Integer> modifier_values;
    public Map<Ability, Integer> scores;

    public BaseAbilitySet(Map<Ability, Integer> base_abilities) {
        this.modifiers = new HashSet<>();
        this.base_scores = new TreeMap<>(Abilities.getDefaultComparator());
        base_scores.putAll(base_abilities);
        this.modifier_values = new TreeMap<>(Abilities.getDefaultComparator());
        this.scores = new TreeMap<>(Abilities.getDefaultComparator());
    }

    public BaseAbilitySet() {
        this(new HashMap<>());
    }

    public BaseAbilitySet(Set<Ability> base_abilities, Integer base_value) {
        this();
        for (Ability ability: base_abilities) this.base_scores.put(ability, base_value);
    }

    public BaseAbilitySet(Set<Ability> base_abilities) {
        this(base_abilities, 10);
    }


    @Override
    public void setBaseScore(Ability ability, Integer value) {
        if (value == null) this.base_scores.remove(ability);
        else this.base_scores.put(ability, value);
        this.reloadScores();
    }

    @Override
    public int getBaseScore(Ability ability) {
        if (base_scores.containsKey(ability)) return base_scores.get(ability);
        return getDefaultScore();
    }

    @Override
    public int getScore(Ability ability) {
        if (scores.containsKey(ability)) return scores.get(ability);
        return getDefaultScore();
    }

    @Override
    public int getModifierTotal(Ability ability) {
        Integer value = modifier_values.get(ability);
        return (value == null) ? 0 : value;
    }

    @Override
    public void removeBaseScore(Ability ability) {
        base_scores.remove(ability);
        this.reloadScores();
    }

    @Override
    public void resetBaseScore(Ability ability) {
        setBaseScore(ability, getDefaultScore());
    }

    @Override
    public Set<Ability> getSpecified() {
        return base_scores.keySet();
    }

    @Override
    public void reloadScores() {
        modifier_values.clear();
        for (AbilityModifier modifier: this.modifiers) {
            for (Ability ability: modifier.getAbilities()) {
                modifier_values.put(
                        ability,
                        modifier.getValue(ability) + modifier_values.getOrDefault(ability, 0));
            }
        }
        for (Ability ability: getSpecified()) {
            scores.put(ability, getBaseScore(ability) + getModifierTotal(ability));
        }
    }

    @Override
    public void addModifier(AbilityModifier modifier) {
        if (!modifiers.contains(modifier)) {
            modifiers.add(modifier);
            this.reloadScores();
        }
    }

    @Override
    public boolean removeModifier(AbilityModifier modifier) {
        boolean found = modifiers.remove(modifier);
        this.reloadScores();
        return found;
    }

    @Override
    public int getDefaultScore() {
        return 10;
    }

    public String toString() {
        StringBuilder string = new StringBuilder("Ability set: ");
        for (Ability ability: getSpecified()) {
            string.append("\n > ").append(ability.getID()).append(": ").append(getScore(ability));
        }
        return string.toString();
    }
}
