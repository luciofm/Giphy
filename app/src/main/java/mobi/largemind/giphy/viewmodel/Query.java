package mobi.largemind.giphy.viewmodel;

public class Query {
    private Type type;
    private String query;
    private boolean reload;

    private Query(Type type, String query, boolean reload) {
        this.type = type;
        this.query = query;
        this.reload = reload;
    }

    public static Query trending() {
        return trending(false);
    }

    public static Query trending(boolean reload) {
        return new Query(Type.TRENDING, null, reload);
    }

    public static Query search(String query) {
        return search(query, false);
    }

    public static Query search(String query, boolean reload) {
        return new Query(Type.SEARCH, query, reload);
    }

    public Type getType() {
        return type;
    }

    public String getQuery() {
        return query;
    }

    public boolean isReload() {
        return reload;
    }

    public enum Type {
        TRENDING,
        SEARCH
    }
}
