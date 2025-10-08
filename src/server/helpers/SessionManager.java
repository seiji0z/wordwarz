package server.helpers;

import WordWarZ.NotLoggedIn;
import server.objects.Session;
import server.servants.GameServant;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> tokenMap = new ConcurrentHashMap<>();
    private static final Map<String, String> userToTokenMap = new ConcurrentHashMap<>();
    private static Session session;

    public static String createSession(String username, boolean isAdmin) {
        String token = UUID.randomUUID().toString();
        Session data = new Session(username, token, isAdmin);
        tokenMap.put(token, data);
        userToTokenMap.put(username, token);
        System.out.println("Session created for " + username);
        return token;
    }

    public static Session getSession(String token) throws NotLoggedIn {
        session = tokenMap.get(token);
        if (session == null) throw new NotLoggedIn();
        return session;
    }

    public static void removeSession(String token) {
        Session session = tokenMap.get(token);
        if (session != null) {
            // Clean up game state first
            GameServant.cleanupExpiredSession(token);

            // Remove from maps
            tokenMap.remove(token);
            userToTokenMap.remove(session.getUsername());
        }
    }

    public static void removeSessionWithoutCleanup(String token) {
        Session session = tokenMap.get(token);
        if (session != null) {

            // Remove from maps
            tokenMap.remove(token);
            userToTokenMap.remove(session.getUsername());
        }
    }

    public static boolean isTokenValid(String token) {
        return tokenMap.containsKey(token);
    }

    public static boolean isUserLoggedIn(String username) {
        return userToTokenMap.containsKey(username);
    }

    public static String getTokenByUsername(String username) {return userToTokenMap.get(username);}
}
