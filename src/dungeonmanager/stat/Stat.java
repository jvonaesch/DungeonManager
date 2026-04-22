package dungeonmanager.stat;

/**
 * Core interface for all stat types in the system.
 * Stats represent character attributes that can be tracked and modified. (abilities, health, etc.)
 *
 * @see dungeonmanager.stat.StandardStat for predefined stat
 * @see dungeonmanager.stat.CustomStat for user-defined stat
 */
public interface Stat {

    String getName();
    String getID();
    
    /**
     * @return the origin identifier indicating where this stat was defined (default, mod, homebrew, etc.)
     */
    String getOriginIdentifier();
    
    /**
     * @return the type category of this stat (eg. "ability", "base_stat", etc.)
     */
    String getType();
    
    /**
     * @return the default value for this stat when not explicitly set
     */
    int getDefaultValue();
}
