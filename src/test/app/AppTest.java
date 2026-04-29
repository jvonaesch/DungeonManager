package test.app;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.session.Session;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.stat.Stat;
import dungeonmanager.stat.StatModifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static dungeonmanager.contentpack.PackLoader.writeToFile;

public abstract class AppTest extends test.AbstractTest {

    protected static final DungeonManagerApp app = new DungeonManagerApp();
    protected static final Session setupSession = newSession();
    protected Session session;

    @BeforeAll
    static void beforeAll() {
        LOG.info("Setting up test environment...");
        generateTestData();
    }

    @BeforeEach
    void setUp() {
        session = newSession();
    }

    public static Session newSession() {
        return new Session(app, TEST_WORKSPACE_PATH);
    }
}
