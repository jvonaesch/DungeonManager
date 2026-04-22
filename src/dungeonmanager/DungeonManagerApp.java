package dungeonmanager;

import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.command.*;
import dungeonmanager.command.commands.RollCommand;
import dungeonmanager.command.commands.StopCommand;
import dungeonmanager.feature.PackLoader;
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

    private final Registries registry;
    private String workingDirectory;
    // private Session session;

    private Scanner console_in;
    private CommandLine command_line;

    private boolean alive;

    public static void main(String[] args) {
        LOG.info("Starting DungeonManager application");

        DungeonManagerApp app = new DungeonManagerApp();
        app.initialize();

        LOG.info("Initialization complete, entering run flow");
        app.run();
    }

    public DungeonManagerApp() {
        this.workingDirectory = DEFAULT_WORKSPACE_PATH;
        try {
            loadDirectory(LIB_PATH, "library");
            loadDirectory(this.workingDirectory, "workspace");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        registry = Registries.get();
    }

    public void initialize() {
        LOG.debug("Initializing application registries and command line context");
        alive = true;

        // session = new Session();
        console_in = new Scanner(System.in);
        command_line = new CommandLine(new CommandContext(this, console_in, registry));

        for (Stat stat : StandardStat.values()) {
            registry.stats.register(stat.getID(), () -> stat);
        }
        LOG.debug("Registered {} standard stats", StandardStat.values().length);

        registry.command.register("roll", () -> new RollCommand());
        registry.command.register("r", () -> registry.command.get("roll"));
        registry.command.register("stop", () -> new StopCommand());
        LOG.debug("Registered command aliases: roll, r, stop");

        LOG.debug("Loading shared feature packs from {}", LIB_PATH);
        PackLoader.loadFeaturesFromLibrary(LIB_PATH);
        LOG.debug("Loading workspace feature pack from {}", workingDirectory);
        PackLoader.loadFromPack(workingDirectory);
        LOG.info("Feature pack loading complete (library + workspace)");
    }

    public void run() {

        // COMMAND PROMPT
        /*while (this.alive) {
            System.out.print("> ");
            command_line.waitForCommand();
            command_line.executeLastCommand();
        }*/
    }

    public void setWorkingDirectory(String directoryPath) throws FileNotFoundException {
        File workspaceDir = new File(directoryPath);
        loadDirectory(workspaceDir.getPath(), "workspace");
        this.workingDirectory = workspaceDir.getPath();
    }

    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    private static void loadDirectory(String path, String label) throws FileNotFoundException {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) throw new FileNotFoundException("Path to " + label + " could not be created");
            LOG.info("Created {} directory at {}", label, dir.getAbsolutePath());
        } else {
            LOG.debug("{} directory already exists at {}", label, dir.getAbsolutePath());
        }
    }

    public void shutdown() {
        LOG.info("Shutting down DungeonManager application");
        this.alive = false;
    }
}
