package dungeonmanager.stat;

public class CustomStat implements Stat {

    private String originIdentifier;
    private String name;
    private String id;

    public CustomStat(String id, String name, String originIdentifier) {
        this.originIdentifier = originIdentifier;
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getOriginIdentifier() {
        return originIdentifier;
    }
}
