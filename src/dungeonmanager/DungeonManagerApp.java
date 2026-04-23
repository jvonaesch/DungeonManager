package dungeonmanager;

import dungeonmanager.session.Session;
import dungeonmanager.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Scanner;

public class DungeonManagerApp {

    private static final Logger LOG = LoggerFactory.getLogger(DungeonManagerApp.class);

    public static final String USER_DIR = System.getProperty("user.home");
    public static final String APP_PATH = USER_DIR + "/DungeonManager/";
    public static final String LIB_PATH = APP_PATH + "library/";
    public static final String DEFAULT_WORKSPACE_PATH = APP_PATH + "workspace/default/";

    public static void main(String[] args) {
        DungeonManagerApp app = new DungeonManagerApp();
        app.run();
    }

    public DungeonManagerApp() {
        LOG.info("Starting DungeonManager application");
        File dir = new File(LIB_PATH);
        if (!dir.exists()) {
            if (!dir.mkdirs()) throw new RuntimeException(
                    "Path to library could not be created! Running without library");
            LOG.info("Created library directory at {}", dir.getAbsolutePath());
        }
        LOG.info("Initialization complete");
    }

    public void run () {
        LOG.info("Running DungeonManager application");
        // TODO: main application loop
        LOG.info("Quitting DungeonManager application");
    }

    public Session getSession(String workspacePath) {
        return new Session(this, workspacePath);
    }

    public Session getSession() {
        return new Session(this, DEFAULT_WORKSPACE_PATH);
    }

    public void shutdown() {
        LOG.info("Shutting down DungeonManager application");
    }
}
