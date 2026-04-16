package dungeonmanager.stat;

public interface ModifiableStatSet extends StatSet {
    public void setBaseScore(Stat ability, Integer value);
    public void removeBaseScore(Stat ability);
    public int getBaseScore(Stat ability);

    public void addModifier(StatModifier modifier);
    public boolean removeModifier(StatModifier modifier);
    public void resetBaseScore(Stat ability);
    public int getModifierTotal(Stat ability);
    public void reloadScores();
}
