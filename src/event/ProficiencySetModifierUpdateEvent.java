package event;

import ability.proficiency.ProficiencySetModifier;

public class ProficiencySetModifierUpdateEvent extends Event {

    public final ProficiencySetModifier modifier;

    public ProficiencySetModifierUpdateEvent(ProficiencySetModifier modifier) {
        super(BaseEventType.PROFICIENCY_SET_MODIFIER_UPDATE);
        this.modifier = modifier;
    }
}
