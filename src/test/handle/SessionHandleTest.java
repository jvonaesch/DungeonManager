package test.handle;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.feature.Feature;
import dungeonmanager.feature.StatModifierSection;
import dungeonmanager.session.Session;
import dungeonmanager.session.SessionHandle;
import dungeonmanager.stat.Stat;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.stat.StatModifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.AbstractTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static dungeonmanager.contentpack.PackLoader.writeToFile;

public abstract class SessionHandleTest extends AbstractTest {

    protected static final DungeonManagerApp app = new DungeonManagerApp();
    protected static final Session setupSession = new Session(app, TEST_WORKSPACE_PATH);
    protected SessionHandle handle;

    @BeforeAll
    static void beforeAll() {
        LOG.info("Setting up test environment...");
        generateTestData();
    }

    @BeforeEach
    void setUp() {
        handle = app.getSessionHandle(TEST_WORKSPACE_PATH);
    }
}

