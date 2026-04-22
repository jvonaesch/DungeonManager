package dungeonmanager.session;

public interface JsonSerializable <T> {
    String toJson();
    static <T> T fromJson(String json) {
        throw new UnsupportedOperationException("Use concrete type static fromJson(String)");
    }
}
