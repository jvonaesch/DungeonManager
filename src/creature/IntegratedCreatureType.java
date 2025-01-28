package creature;

public enum IntegratedCreatureType implements CreatureType {
    OWLBEAR("base:creature:owlbear", "owlbear");


    private String id;

    private IntegratedCreatureType(String id, String name) {
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }
}
