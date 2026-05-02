package test.app;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.session.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import test.Test;

public abstract class AppTest extends Test {

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
