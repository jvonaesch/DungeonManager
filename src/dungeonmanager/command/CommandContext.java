package dungeonmanager.command;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.registry.Registries;

import java.util.Scanner;

public class CommandContext {

    protected final DungeonManagerApp app;
    protected final Scanner input;
    protected final Registries registries;

    public CommandContext(DungeonManagerApp app, Scanner input, Registries registries) {
        this.app = app;
        this.input = input;
        this.registries = registries;
    }
}
