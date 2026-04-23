package dungeonmanager;

import dungeonmanager.session.Session;
import dungeonmanager.stat.StandardStat;
import dungeonmanager.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DungeonManagerApp {

    private static final Logger LOG = LoggerFactory.getLogger(DungeonManagerApp.class);

    public static final String USER_DIR = System.getProperty("user.home");
    public static final String APP_PATH = USER_DIR + "/DungeonManager/";
    public static final String LIB_PATH = APP_PATH + "library/";
    public static final String DEFAULT_WORKSPACE_PATH = APP_PATH + "workspace/default/";

    private Scanner console_in;
    private CommandLine command_line;

    private boolean alive = true;

    public static void main(String[] args) {
        DungeonManagerApp app = new DungeonManagerApp();
        app.run();
    }

    public DungeonManagerApp() {
        LOG.info("Starting DungeonManager application");
        try {
            requireDirectory(LIB_PATH, "library");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        LOG.debug("Initializing application registries and command line context");
        console_in = new Scanner(System.in);
        // command_line = new CommandLine(new CommandContext(this, console_in, registry));

        LOG.debug("Registered {} standard stat", StandardStat.values().length);

        // registry.command.register("roll", () -> new RollCommand());
        // registry.command.register("r", () -> registry.command.get("roll"));
        // registry.command.register("stop", () -> new StopCommand());
        // LOG.debug("Registered commands");

        LOG.info("Initialization complete");
    }

    public void run() {
        LOG.info("Running DungeonManager application");
        // COMMAND PROMPT
        /*while (this.alive) {
            System.out.print("> ");
            command_line.waitForCommand();
            command_line.executeLastCommand();
        }*/
        LOG.info("Quitting DungeonManager application");
    }

    /*public void setWorkingDirectory(String directoryPath) throws FileNotFoundException {
        File workspaceDir = new File(directoryPath);
        requireDirectory(workspaceDir.getPath(), "workspace");
        this.workingDirectory = workspaceDir.getPath();
    }*/

    /*public String getWorkingDirectory() {
        return this.workingDirectory;
    }*/

    public Session getSession(String workspacePath) {
        return new Session(this, workspacePath);
    }

    public Session getSession() {
        return new Session(this, DEFAULT_WORKSPACE_PATH);
    }

    /**
     * Ensure a directory exists at the given path, creating it if necessary.
     * @param path
     * @param label
     * @throws FileNotFoundException
     */
    private static void requireDirectory(String path, String label) throws FileNotFoundException {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) throw new FileNotFoundException("Path to " + label + " could not be created");
            LOG.info("Created {} directory at {}", label, dir.getAbsolutePath());
        }
    }

    public void shutdown() {
        LOG.info("Shutting down DungeonManager application");
        this.alive = false;
    }
}
