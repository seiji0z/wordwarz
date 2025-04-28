package server.objects;

import server.database.DBManager;
import java.sql.SQLException;

public class GameConfig {
    private static int waitingTime = 10; // default
    private static int roundDuration = 30; // default

    static {
        loadConfig();
    }

    public static void loadConfig() {
        int[] config = DBManager.loadGameConfig();
        waitingTime = config[0];
        roundDuration = config[1];
    }

    public static int getWaitingTime() {
        return waitingTime;
    }

    public static int getRoundDuration() {
        return roundDuration;
    }

    public static void updateConfig(int newWaitingTime, int newRoundDuration) throws SQLException {
        DBManager.updateGameConfig(newWaitingTime, newRoundDuration);
        waitingTime = newWaitingTime;
        roundDuration = newRoundDuration;
    }
}