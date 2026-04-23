package dungeonmanager.command;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.registry.SessionRegistry;

import java.util.Scanner;

public class CommandContext {

    protected final DungeonManagerApp app;
    protected final Scanner input;
    protected final SessionRegistry registries;

    public CommandContext(DungeonManagerApp app, Scanner input, SessionRegistry registries) {
        this.app = app;
        this.input = input;
        this.registries = registries;
    }
}
