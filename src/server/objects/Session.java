package server.objects;

public class Session {
    private final String username;
    private final String token;
    private final boolean isAdmin;

    public Session(String username, String token, boolean isAdmin) {
        this.username = username;
        this.token = token;
        this.isAdmin = isAdmin;

    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
