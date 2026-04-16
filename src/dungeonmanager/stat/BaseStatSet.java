package dungeonmanager.stat;

import java.util.*;

public class BaseStatSet implements ModifiableStatSet {

    public Set<StatModifier> modifiers;
    public Map<Stat, Integer> base_scores;
    public Map<Stat, Integer> modifier_values;
    public Map<Stat, Integer> scores;

    public BaseStatSet(Map<Stat, Integer> base_abilities) {
        this.modifiers = new HashSet<>();
        this.base_scores = new TreeMap<>(Stats.getDefaultComparator());
        base_scores.putAll(base_abilities);
        this.modifier_values = new TreeMap<>(Stats.getDefaultComparator());
        this.scores = new TreeMap<>(Stats.getDefaultComparator());
    }

    public BaseStatSet() {
        this(new HashMap<>());
    }

    public BaseStatSet(Set<Stat> base_abilities, Integer base_value) {
        this();
        for (Stat ability: base_abilities) this.base_scores.put(ability, base_value);
    }

    public BaseStatSet(Set<Stat> base_abilities) {
        this(base_abilities, 10);
    }


    @Override
    public void setBaseScore(Stat ability, Integer value) {
        if (value == null) this.base_scores.remove(ability);
        else this.base_scores.put(ability, value);
        this.reloadScores();
    }

    @Override
    public int getBaseScore(Stat ability) {
        if (base_scores.containsKey(ability)) return base_scores.get(ability);
        return getDefaultScore();
    }

    @Override
    public int getScore(Stat ability) {
        if (scores.containsKey(ability)) return scores.get(ability);
        return getDefaultScore();
    }

    @Override
    public int getModifierTotal(Stat ability) {
        Integer value = modifier_values.get(ability);
        return (value == null) ? 0 : value;
    }

    @Override
    public void removeBaseScore(Stat ability) {
        base_scores.remove(ability);
        this.reloadScores();
    }

    @Override
    public void resetBaseScore(Stat ability) {
        setBaseScore(ability, getDefaultScore());
    }

    @Override
    public Set<Stat> getSpecified() {
        return base_scores.keySet();
    }

    @Override
    public void reloadScores() {
        modifier_values.clear();
        for (StatModifier modifier: this.modifiers) {
            for (Stat ability: modifier.getAbilities()) {
                modifier_values.put(
                        ability,
                        modifier.getValue(ability) + modifier_values.getOrDefault(ability, 0));
            }
        }
        for (Stat ability: getSpecified()) {
            scores.put(ability, getBaseScore(ability) + getModifierTotal(ability));
        }
    }

    @Override
    public void addModifier(StatModifier modifier) {
        if (!modifiers.contains(modifier)) {
            modifiers.add(modifier);
            this.reloadScores();
        }
    }

    @Override
    public boolean removeModifier(StatModifier modifier) {
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
        for (Stat stat : getSpecified()) {
            string.append("\n > ").append(stat.getID()).append(": ").append(getScore(stat));
        }
        return string.toString();
    }
}
