package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;

public class ClientCallback extends ClientCallbackPOA {
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    // TEMP

    @Override
    public void onQueueUpdated(int playerCount) {
        System.out.println("[Callback] Players in queue: " + playerCount);
    }

    @Override
    public void onLeaderboardUpdated(Player[] leaderboard) {
        System.out.println("[Callback] Leaderboard updated:");
        for (Player p : leaderboard) {
            System.out.println("  " + p.username + ": " + p.wins + " wins");
        }
    }

    @Override
    public void onGameCountdown(int secondsLeft) {
        System.out.println("[Callback] Game starting in: " + secondsLeft + "s");
    }

    @Override
    public void onRoundStarted(String[] wordPlaceholder) {
        System.out.print("[Callback] Round started. Word: ");
        for (String s : wordPlaceholder) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    @Override
    public void onRoundLost(String word, String winner) {

    }

    @Override
    public void onRoundWon(String word) {

    }

    @Override
    public void onRoundDrawn(String word) {

    }

    @Override
    public void onGameLost(String winnerUsername) {

    }

    @Override
    public void onGameWon(String winnerUsername) {

    }

    @Override
    public void onForceLogout() {
        System.out.println("[Callback] Force logout requested");
    }
}
