package dungeonmanager;

import dungeonmanager.ability.*;
import dungeonmanager.command.Command;
import dungeonmanager.command.CommandParser;
import dungeonmanager.command.CommandRecord;
import dungeonmanager.command.commands.RollCommand;
import dungeonmanager.command.commands.StopCommand;
import dungeonmanager.creature.Entity;
import dungeonmanager.creature.IntegratedEntityType;
import dungeonmanager.registry.Registries;
import dungeonmanager.session.Session;

import java.io.File;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DungeonManagerApp {

    static final String USER_DIR = System.getProperty("user.home");
    static final String LIB_PATH = USER_DIR + "/DungeonManagerLibrary/";

    private Registries registry;
    private Session session;

    private Scanner console_in;
    private Queue<CommandRecord> command_queue;

    private boolean alive;

    public static void main(String[] args) {

        DungeonManagerApp app = new DungeonManagerApp();
        app.initialize();

        app.run();
    }

    public DungeonManagerApp() {
        loadLibrary();
        registry = Registries.getInstance();
    }

    public void initialize() {
        alive = true;

        session = new Session();
        console_in = new Scanner(System.in);
        command_queue = new ConcurrentLinkedQueue<> ();

        registry.command.register("roll", () -> new RollCommand());
        registry.command.register("r", () -> registry.command.get("roll"));
        registry.command.register("stop", () -> new StopCommand());
    }

    public void run() {

        /* INITIAL DEBUG CODE GOES HERE */
        test1();

        while (this.alive) {
            System.out.print("> ");
            this.waitForCommand();
            this.executeLastCommand();
        }
    }

    public void waitForCommand () {
        String command_in = console_in.nextLine();
        command_queue.add(CommandParser.readCommand(this, command_in));
    }

    public boolean executeLastCommand() {
        CommandRecord record = command_queue.poll();
        String command_id = record.getCommand();
        String command_return;

        try {
            Command command = registry.command.get(command_id);
            boolean success = command.execute(record);
            System.out.println(record.getMessage());
            return success;
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.out);
            return false;
        }
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

    public void test1() {
        Ability cosmicAwareness = new CustomAbility(
                "custom:dungeonmanager.ability:cosmic_awareness",
                "cosmic awareness",
                "COS");
        registry.ability.register(cosmicAwareness.getID(), cosmicAwareness);

        Entity monster = new Entity(IntegratedEntityType.OWLBEAR,"Bert, the Owlbear");

        monster.getAbilitySet().removeBaseScore(StandardAbility.INT);
        monster.getAbilitySet().setBaseScore(cosmicAwareness, 20);

        AbilityModifier modifier = new AbilityModifier();
        monster.addScoreModifier(modifier);

        modifier.setValue(StandardAbility.STR, 8);
        modifier.setValue(StandardAbility.CHA, 1);
        modifier.setValue(StandardAbility.INT, 4);

        System.out.println(monster);

        monster.setBaseAbilityScore(StandardAbility.INT, 5);
        System.out.println(monster);

        monster.getAbilitySet().resetBaseScore(StandardAbility.INT);
        System.out.println(monster);
    }
}
