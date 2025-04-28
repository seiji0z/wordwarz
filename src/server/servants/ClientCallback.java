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
    public void onRoundEnded(String roundResult, String winner) {
        System.out.println("[Callback] Round ended. Result: " + roundResult);
        if (winner != null && !winner.isEmpty()) {
            System.out.println("  Winner: " + winner);
        }
    }

    @Override
    public void onGameEnded(String winnerUsername) {
        System.out.println("[Callback] Game ended. Winner: " +
                (winnerUsername != null ? winnerUsername : "No winner"));
    }

    @Override
    public void onForceLogout() {
        System.out.println("[Callback] Force logout requested");
    }
}