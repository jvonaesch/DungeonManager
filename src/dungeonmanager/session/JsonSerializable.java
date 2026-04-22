package dungeonmanager.session;

public interface JsonSerializable{
    String toJson();
    static <T> T fromJson(String json) {
        throw new UnsupportedOperationException("fromJson() must be implemented by the concrete class");
    };
}
