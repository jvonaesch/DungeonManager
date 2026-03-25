package dungeonmanager.command;

import javax.lang.model.element.Name;
import java.util.List;
import java.util.Set;

public interface Command <T> {

    public boolean execute(CommandRecord <T> record);

    // public List<Class> getParameterConfig();
    // public List<String> getParameterNames();
}
