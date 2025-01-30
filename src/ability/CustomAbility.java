package ability;

public class CustomAbility implements Ability {

    private String ID;
    private String name;
    private String shortName;

    public CustomAbility (String ID, String name, String shortName) {
        this.ID = ID;
        this.name = name;
        this.shortName = shortName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public String getID() {
        return ID;
    }
}
