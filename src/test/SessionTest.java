package test;

import dungeonmanager.creature.IntegratedCreatureType;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.registry.Registries;
import dungeonmanager.session.Session;
import dungeonmanager.session.CreatureSnapshot;
import dungeonmanager.session.SessionSnapshot;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.stats.StatModifier;

import java.util.HashMap;
import java.util.Map;

public class SessionTest {

    public static void main(String[] args) {
        test_session_owner();
        System.out.println("SessionTest passed");
    }

    public static void test_session_owner() {
        registerStandardStats();

        Session session = new Session();

        Map<String, Integer> baseStats = new HashMap<>();
        baseStats.put("STR", 15);
        baseStats.put("CHA", 8);

        CreatureSnapshot created = session.createCreature("Hero", IntegratedCreatureType.DEFAULT, baseStats);
        if (!created.getId().equals(session.getSelectedCreatureId())) {
            throw new IllegalStateException("New creature should be selected after creation");
        }
        if (created.getStat("STR") != 15) {
            throw new IllegalStateException("Expected STR 15 but got " + created.getStat("STR"));
        }
        if (created.getStat("CHA") != 8) {
            throw new IllegalStateException("Expected CHA 8 but got " + created.getStat("CHA"));
        }

        CreatureSnapshot renamed = session.renameCreature(created.getId(), "Hero Prime");
        if (!"Hero Prime".equals(renamed.getName())) {
            throw new IllegalStateException("Rename did not update snapshot name");
        }

        CreatureSnapshot retyped = session.changeCreatureType(created.getId(), IntegratedCreatureType.OWLBEAR);
        if (!IntegratedCreatureType.OWLBEAR.getID().equals(retyped.getTypeId())) {
            throw new IllegalStateException("Type change did not update snapshot type");
        }

        CreatureSnapshot edited = session.setBaseStat(created.getId(), "STR", 16);
        if (edited.getStat("STR") != 16) {
            throw new IllegalStateException("Expected STR 16 after base stat edit but got " + edited.getStat("STR"));
        }

        Feature feat = new Feature("feat:charisma_boost", "Charisma Boost", "Gain charisma")
                .addSection(new StatModifierSection(
                        "cha_bonus",
                        "CHA Bonus",
                        "CHA +2",
                        new StatModifier().setValue(StandardStat.CHA, 2),
                        false
                ));

        CreatureSnapshot afterFeat = session.addFeature(created.getId(), feat);
        if (afterFeat == null) {
            throw new IllegalStateException("Expected feat to be added");
        }
        if (afterFeat.getStat("CHA") != 10) {
            throw new IllegalStateException("Expected CHA 10 after feat but got " + afterFeat.getStat("CHA"));
        }
        if (afterFeat.getFeatures().size() != 1) {
            throw new IllegalStateException("Expected one feat in snapshot but got " + afterFeat.getFeatures().size());
        }

        CreatureSnapshot afterRemoval = session.removeFeature(created.getId(), feat.ID);
        if (afterRemoval == null) {
            throw new IllegalStateException("Expected feat removal to succeed");
        }
        if (afterRemoval.getStat("CHA") != 8) {
            throw new IllegalStateException("Expected CHA 8 after feat removal but got " + afterRemoval.getStat("CHA"));
        }

        if (!session.selectCreature(created.getId())) {
            throw new IllegalStateException("Expected creature selection to succeed");
        }
        if (session.getSelectedCreatureSnapshot() == null) {
            throw new IllegalStateException("Expected selected creature snapshot");
        }

        SessionSnapshot snapshot = session.snapshot();
        if (snapshot.getCreatureCount() != 1) {
            throw new IllegalStateException("Expected one creature in the session snapshot");
        }
        if (snapshot.getCreature(created.getId()) == null) {
            throw new IllegalStateException("Expected creature lookup by ID in snapshot");
        }

        boolean mapImmutable = false;
        try {
            snapshot.getCreatures().add(created);
        } catch (UnsupportedOperationException expected) {
            mapImmutable = true;
        }
        if (!mapImmutable) {
            throw new IllegalStateException("Expected creature list snapshot to be immutable");
        }

        boolean statsImmutable = false;
        try {
            created.getStats().put("STR", 99);
        } catch (UnsupportedOperationException expected) {
            statsImmutable = true;
        }
        if (!statsImmutable) {
            throw new IllegalStateException("Expected stat snapshot map to be immutable");
        }

        if (!session.deleteCreature(created.getId())) {
            throw new IllegalStateException("Expected creature deletion to succeed");
        }
        if (session.getSelectedCreatureId() != null) {
            throw new IllegalStateException("Expected selected creature to clear after deleting the last creature");
        }
    }

    private static void registerStandardStats() {
        for (StandardStat stat : StandardStat.values()) {
            Registries.get().stats.register(stat.getID(), stat);
        }
    }
}

