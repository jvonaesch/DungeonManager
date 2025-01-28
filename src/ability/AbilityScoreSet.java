package ability;

import java.util.*;

public class AbilityScoreSet {

    public Set<AbilityScoreSetModifier> modifiers;
    public Map<Ability, Integer> base_scores;
    public Map<Ability, Integer> scores;

    public AbilityScoreSet(Map<Ability, Integer> base_abilities) {
        this.modifiers = new HashSet<AbilityScoreSetModifier> ();
        this.base_scores = new HashMap<Ability, Integer> (base_abilities);
        this.scores = new HashMap<Ability, Integer> ();
    }

    public AbilityScoreSet() {
        this(new HashMap<Ability, Integer> ());
    }

    public AbilityScoreSet(Set<Ability> base_abilities, Integer base_value) {
        this();
        for (Ability ability: base_abilities) this.base_scores.put(ability, base_value.intValue());
    }

    public AbilityScoreSet(Set<Ability> base_abilities) {
        this(base_abilities, 10);
    }

    public AbilityScoreSet registerAbility(Ability ability, int value) {
        this.base_scores.put(ability, value);
        return this;
    }

    public AbilityScoreSet registerAbility(Ability ability) {
        return this.registerAbility(ability, 10);
    }

    public boolean unregisterAbility(Ability ability) {
        if (!base_scores.containsKey(ability)) return false;
        base_scores.remove(ability);
        return true;
    }

    public void addScoreModifier(AbilityScoreSetModifier modifier) {
        if (!modifiers.contains(modifier)) {
            modifiers.add(modifier);
            this.reloadScoreValues();
        }
    }

    public boolean removeScoreModifier(AbilityScoreSetModifier modifier) {
        boolean found = modifiers.remove(modifier);
        this.reloadScoreValues();
        return found;
    }

    public void reloadScoreValues() {
        for (Ability ability: this.base_scores.keySet()) {
            scores.put(ability, base_scores.get(ability));
        }
        for (AbilityScoreSetModifier modifier: this.modifiers) {
            for (Ability ability: modifier.getAbilities()) {
                if (scores.containsKey(ability)) {
                    int value = scores.get(ability);
                    value += modifier.getValue(ability);
                    scores.put(ability, value);
                }
            }
        }
    }

    public Integer getModifier(Ability ability) {
        Integer value = getValue(ability);
        return value == null ? null : (value - 10) / 2;
    }

    public Integer getBaseValue(Ability ability) {
        if (base_scores.containsKey(ability)) return base_scores.get(ability);
        return null;
    }

    public boolean setBaseValue(Ability ability, int value) {
        if (base_scores.containsKey(ability)) {
            base_scores.put(ability, value);
            this.reloadScoreValues();
            return true;
        }
        return false;
    }

    public String toString() {
        String string = "Ability set: ";
        for (Ability ability: base_scores.keySet()) {
            string += "\n> "+ability.getShortName()+": "+base_scores.get(ability)+"";
        }
        return string;
    }

    public Integer getValue(Object ability) {
        if (!(ability instanceof Ability)) throw
                new IllegalArgumentException("Key of wrong type: "+ability.getClass().getName());
        if (scores.containsKey(ability)) return scores.get(ability);
        return null;
    }

    public int size() {
        return base_scores.size();
    }
    public boolean isEmpty() {
        return base_scores.isEmpty();
    }
}
