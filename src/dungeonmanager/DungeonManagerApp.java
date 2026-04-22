package dungeonmanager;

import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.command.*;
import dungeonmanager.command.commands.RollCommand;
import dungeonmanager.command.commands.StopCommand;
import dungeonmanager.contentPack.PackLoader;
import dungeonmanager.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DungeonManagerApp {

    private static final Logger LOG = LoggerFactory.getLogger(DungeonManagerApp.class);

    static final String USER_DIR = System.getProperty("user.home");
    static final String APP_PATH = USER_DIR + "/DungeonManager/";
    static final String LIB_PATH = APP_PATH + "library/";
    static final String DEFAULT_WORKSPACE_PATH = APP_PATH + "workspace/default/";

    private static final Registries registry = Registries.get();
    private String workingDirectory;
    // private Session session;

    private Scanner console_in;
    private CommandLine command_line;

    private boolean alive = true;

    public static void main(String[] args) {
        DungeonManagerApp app = new DungeonManagerApp();
        app.run();
    }

    public DungeonManagerApp(
            String workingDirectory
    ) {
        LOG.info("Starting DungeonManager application");
        this.workingDirectory = workingDirectory;
        try {
            requireDirectory(LIB_PATH, "library");
            requireDirectory(this.workingDirectory, "workspace");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        LOG.debug("Initializing application registries and command line context");
        console_in = new Scanner(System.in);
        command_line = new CommandLine(new CommandContext(this, console_in, registry));

        for (Stat stat : StandardStat.values()) {
            registry.stats.register(stat.getID(), () -> stat);
        }
        LOG.debug("Registered {} standard stats", StandardStat.values().length);

        registry.command.register("roll", () -> new RollCommand());
        registry.command.register("r", () -> registry.command.get("roll"));
        registry.command.register("stop", () -> new StopCommand());
        LOG.debug("Registered commands");

        LOG.debug("Loading shared content packs from {}", LIB_PATH);
        PackLoader.loadLibrary(LIB_PATH);
        LOG.debug("Loading workspace content pack from {}", workingDirectory);
        PackLoader.loadFromPack(workingDirectory);
        LOG.debug("Content pack loading complete: {} features loaded", registry.feature.getSize());
        LOG.info("Initialization complete");
    }

    public DungeonManagerApp() {
        this(DEFAULT_WORKSPACE_PATH);
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

    public void setWorkingDirectory(String directoryPath) throws FileNotFoundException {
        File workspaceDir = new File(directoryPath);
        requireDirectory(workspaceDir.getPath(), "workspace");
        this.workingDirectory = workspaceDir.getPath();
    }

    public String getWorkingDirectory() {
        return this.workingDirectory;
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
