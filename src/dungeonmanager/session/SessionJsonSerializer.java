package dungeonmanager.session;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @deprecated Use {@link SessionSnapshot} directly.
 */
@Deprecated
public final class SessionJsonSerializer {

    private SessionJsonSerializer() {
    }

    public static String toJson(SessionSnapshot snapshot) {
        return snapshot.toJson();
    }

    public static SessionSnapshot fromJson(String json) {
        return SessionSnapshot.fromJson(json);
    }

    public static void save(Path workspacePath, SessionSnapshot snapshot) throws IOException {
        snapshot.save(workspacePath);
    }

    public static SessionSnapshot load(Path workspacePath) throws IOException {
        return SessionSnapshot.load(workspacePath);
    }
}

