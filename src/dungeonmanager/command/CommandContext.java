package dungeonmanager.command;

import dungeonmanager.DungeonManagerApp;
import dungeonmanager.library.SessionLibrary;

import java.util.Scanner;

public class CommandContext {

    protected final DungeonManagerApp app;
    protected final Scanner input;
    protected final SessionLibrary registries;

    public CommandContext(DungeonManagerApp app, Scanner input, SessionLibrary registries) {
        this.app = app;
        this.input = input;
        this.registries = registries;
    }
}
