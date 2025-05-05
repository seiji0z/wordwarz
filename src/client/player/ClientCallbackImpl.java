package client.player;

import WordWarZ.ClientCallbackPOA;
import client.player.view.QueueView;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;

public class ClientCallbackImpl extends ClientCallbackPOA {
    private final QueueView queueView;

    public ClientCallbackImpl(QueueView q, Stage stage, String playerToken, ORB orb) {
        this.queueView = q;
    }

    @Override
    public void onQueueUpdated(int playerCount) {
        if (queueView != null) {
            queueView.updatePlayerCount(playerCount);
        }
    }

    @Override
    public void onGameCountdown(int secondsLeft) {
        if (queueView != null) {
            queueView.updateTimer(secondsLeft);
        }
    }

    @Override
    public void onRoundStarted(int wordPlaceholder) {
        // TODO: Transition to gameplay view
    }

    @Override
    public void onRoundEnded(String roundResult, String winner) {
        // Optional future: round end handling
    }

    @Override
    public void onGameEnded(String winnerUsername) {
        // Optional future: game over handling
    }

    @Override
    public void onLeaderboardUpdated(WordWarZ.Player[] leaderboard) {
        // Optional future: update leaderboard
    }

    @Override
    public void onForceLogout() {
        // Optional future: logout force handling
    }
}
