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
        try {
            app.run();
        } finally {
            app.shutdown();
        }
    }

    public DungeonManagerApp() {
        LOG.info("Starting DungeonManager application");
        ensureDirectory(LIB_PATH, "library");
        ensureDirectory(DEFAULT_WORKSPACE_PATH, "default workspace");
        LOG.info("Initialization complete");
    }

    private void ensureDirectory(Path path, String label) {
        File dir = path.toFile();
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new RuntimeException("Path to " + label + " is a file: " + dir.getAbsolutePath());
            }
            return;
        }
        if (!dir.mkdirs()) {
            throw new RuntimeException("Path to " + label + " could not be created! Running without " + label);
        }
        LOG.info("Created {} directory at {}", label, dir.getAbsolutePath());
    }

    public synchronized void run() {
        run(DEFAULT_WORKSPACE_PATH);
    }

    public synchronized void run(Path workspacePath) {
        LOG.info("Running DungeonManager application for workspace {}", workspacePath);
        SessionHandle activeSessionHandle = getSessionHandle(workspacePath);
        LOG.info("Active session initialized. Selected creature: {}", activeSessionHandle.getSelectedCreatureId());
        // TODO: main application loop / backend host
        LOG.info("Quitting DungeonManager application");
    }

    public SessionHandle getSessionHandle(Path workspacePath) {
        Session session = new Session(this, workspacePath);
        return new SessionHandle(session);
    }
    
    @SuppressWarnings("unused")
    public SessionHandle getSessionHandle(Path workspacePath, Set<String> contentPacks) {
        Session session = new Session(this, workspacePath, contentPacks);
        return new SessionHandle(session);
    }

    public void shutdown() {
        LOG.info("Shutting down DungeonManager application");
    }
}
