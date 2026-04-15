package dungeonmanager;

import dungeonmanager.command.*;
import dungeonmanager.command.commands.RollCommand;
import dungeonmanager.command.commands.StopCommand;
import dungeonmanager.registry.Registries;
import dungeonmanager.session.Session;

import java.io.File;
import java.util.Scanner;

public class DungeonManagerApp {

    static final String USER_DIR = System.getProperty("user.home");
    static final String LIB_PATH = USER_DIR + "/DungeonManagerLibrary/";

    private Registries registry;
    private Session session;

    private Scanner console_in;
    private CommandLine command_line;

    private boolean alive;

    public static void main(String[] args) {

        DungeonManagerApp app = new DungeonManagerApp();
        app.initialize();

        app.run();
    }

    public DungeonManagerApp() {
        loadLibrary();
        registry = Registries.get();
    }

    public void initialize() {
        alive = true;

        session = new Session();
        console_in = new Scanner(System.in);
        command_line = new CommandLine(new CommandContext(this, console_in, registry));

        registry.command.register("roll", () -> new RollCommand());
        registry.command.register("r", () -> registry.command.get("roll"));
        registry.command.register("stop", () -> new StopCommand());
    }

    public void run() {

        /* INITIAL DEBUG CODE GOES HERE */
        //Tests.test1();
        Tests.test2();

        // COMMAND PROMPT
        /*while (this.alive) {
            System.out.print("> ");
            command_line.waitForCommand();
            command_line.executeLastCommand();
        }*/
    }

    public static void loadLibrary() {
        File lib_dir = new File(LIB_PATH);
        if (!lib_dir.exists()) {
            lib_dir.mkdirs();
        }
    }

    public void shutdown() {
        this.alive = false;
    }
}
