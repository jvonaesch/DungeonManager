package dungeonmanager;

import dungeonmanager.session.Session;
import dungeonmanager.session.SessionHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

public class DungeonManagerApp {

    private static final Logger LOG = LoggerFactory.getLogger(DungeonManagerApp.class);

    public static final Path USER_DIR = Path.of(System.getProperty("user.home"));
    public static final Path APP_PATH = USER_DIR.resolve("DungeonManager");
    public static final Path LIB_PATH = APP_PATH.resolve("library");
    public static final Path DEFAULT_WORKSPACE_PATH = APP_PATH.resolve("workspace/default");

    public static void main(String[] args) {
        DungeonManagerApp app = new DungeonManagerApp();
        app.run();
    }

    public DungeonManagerApp() {
        LOG.info("Starting DungeonManager application");
        File dir = LIB_PATH.toFile();
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

    public SessionHandle getSessionHandle(Path workspacePath) {
        Session session = new Session(this, workspacePath);
        return new SessionHandle(session);
    }

    public SessionHandle getSessionHandle() {
        return getSessionHandle(DEFAULT_WORKSPACE_PATH);
    }

    public SessionHandle getSessionHandle(Path workspacePath, Set<String> contentPacks) {
        Session session = new Session(this, workspacePath, contentPacks);
        return new SessionHandle(session);
    }

    public void shutdown() {
        LOG.info("Shutting down DungeonManager application");
    }
}
