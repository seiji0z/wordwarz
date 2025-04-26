package server;

import WordWarZ.NotLoggedIn;
import server.objects.Session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> tokenMap = new ConcurrentHashMap<>();
    private static final Map<String, String> userToTokenMap = new ConcurrentHashMap<>();

    public static String createSession(String username, boolean isAdmin) {
        String token = UUID.randomUUID().toString();
        Session data = new Session(username, token, isAdmin);
        tokenMap.put(token, data);
        userToTokenMap.put(username, token);
        return token;
    }

    public static Session getSession(String token) throws NotLoggedIn {
        Session session = tokenMap.get(token);
        if (session == null) throw new NotLoggedIn();
        return session;
    }

    public static void removeSession(String token) {
        Session session = tokenMap.remove(token);
        if (session != null) {
            userToTokenMap.remove(session.getUsername());
        }
    }

    public static boolean isTokenValid(String token) {
        return tokenMap.containsKey(token);
    }


    public static boolean isUserLoggedIn(String username) {
        return userToTokenMap.containsKey(username);
    }
}

