package dungeonmanager;

import dungeonmanager.stats.Stat;
import dungeonmanager.stats.StandardStat;
import dungeonmanager.command.*;
import dungeonmanager.command.commands.RollCommand;
import dungeonmanager.command.commands.StopCommand;
import dungeonmanager.registry.Registries;
import test.FeatureTest;
import test.StatSetTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DungeonManagerApp {

    private static final Logger LOG = LoggerFactory.getLogger(DungeonManagerApp.class);

    static final String USER_DIR = System.getProperty("user.home");
    static final String LIB_PATH = USER_DIR + "/DungeonManager/";

    private final Registries registry;
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
        try {
            loadLibrary();
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
    }

    public void run() {
        LOG.info("Running current debug test harness");

        // INITIAL DEBUG CODE
        StatSetTest.test1();
        FeatureTest.test_modifiers();
        LOG.info("Debug test harness completed");

        // COMMAND PROMPT
        /*while (this.alive) {
            System.out.print("> ");
            command_line.waitForCommand();
            command_line.executeLastCommand();
        }*/
    }

    public static void loadLibrary() throws FileNotFoundException {
        File lib_dir = new File(LIB_PATH);
        if (!lib_dir.exists()) {
            if (!lib_dir.mkdirs()) throw new FileNotFoundException("Path to library could not be created");
            LOG.info("Created library directory at {}", lib_dir.getAbsolutePath());
        } else {
            LOG.debug("Library directory already exists at {}", lib_dir.getAbsolutePath());
        }
    }

    public void shutdown() {
        LOG.info("Shutting down DungeonManager application");
        this.alive = false;
    }
}
