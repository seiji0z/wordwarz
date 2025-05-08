package server.helpers;

import server.objects.Game;
import server.objects.GameConfig;
import server.servants.GameServant;
import WordWarZ.NoOpponentFound;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class QueueManager {
    private static List<String> waitingPlayers = new ArrayList<>();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> countdownTask;
    private static boolean countdownStarted = false;
    private static int remainingTime = 0;

    public static synchronized void joinQueue(String username) {
        if (!waitingPlayers.contains(username)) {
            waitingPlayers.add(username);
            System.out.println("Player " + username + " joined queue. Current size: " + waitingPlayers.size());
            broadcastQueueUpdate();

            if (!countdownStarted) {
                System.out.println("Starting countdown for queue");
                startCountdown();
            }
        } else {
            System.out.println("Player " + username + " already in queue");
        }
    }

    public static synchronized void leaveQueue(String username) {
        if (waitingPlayers.remove(username)) {
            System.out.println("Player " + username + " left queue. Current size: " + waitingPlayers.size());
            broadcastQueueUpdate();

            if (waitingPlayers.isEmpty() && countdownStarted) {
                System.out.println("Queue empty, canceling countdown");
                cancelCountdown();
            }
        }
    }

    private static void startCountdown() {
        remainingTime = GameConfig.getWaitingTime();
        countdownStarted = true;

        countdownTask = scheduler.scheduleAtFixedRate(() -> {
            remainingTime--;
            System.out.println("Countdown: " + remainingTime + " seconds remaining");
            broadcastCountdownUpdate();

            if (remainingTime <= 0) {
                System.out.println("Countdown finished, processing queue");
                try {
                    startGame();
                } catch (NoOpponentFound e) {
                    System.err.println("No opponent found: " + e.reason);
                }
                countdownTask.cancel(false);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private static void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel(false);
        }
        countdownStarted = false;
        remainingTime = 0;
        System.out.println("Countdown canceled");
    }

    private static synchronized void startGame() throws NoOpponentFound {
        if (waitingPlayers.size() == 1) {
            String username = waitingPlayers.get(0);
            waitingPlayers.clear();
            countdownStarted = false;
            cancelCountdown();
            GameServant.notifyNoOpponent(username);
            throw new NoOpponentFound("No opponent found after countdown expired");
        } else if (waitingPlayers.size() >= 2) {
            System.out.println("Starting game with players: " + waitingPlayers);

            System.out.println("Attempting to create game. Countdown started: " + countdownStarted + ", Remaining time: " + remainingTime);

            List<String> playerTokens = waitingPlayers.stream()
                    .map(SessionManager::getTokenByUsername)
                    .collect(Collectors.toList());

            List<String> playersToStart = new ArrayList<>(waitingPlayers);
            waitingPlayers.clear();
            countdownStarted = false;

            cancelCountdown();

            GameServant.createAndStartGame(playersToStart);
        }
    }

    private static void broadcastQueueUpdate() {
        System.out.println("Broadcasting queue update: " + waitingPlayers.size() + " players");
        GameServant.notifyQueueUpdate(waitingPlayers.size());
    }

    private static void broadcastCountdownUpdate() {
        System.out.println("Broadcasting countdown update: " + remainingTime + " seconds");
        GameServant gameServant = new GameServant();
        gameServant.notifyCountdownUpdate(remainingTime);
    }

    public static synchronized int getQueueSize() {
        System.out.println("Queue size requested. Current players: " + waitingPlayers);
        return waitingPlayers.size();
    }

    public static synchronized int getRemainingTime() {
        return remainingTime;
    }

    public static synchronized boolean isInQueue(String username) {
        return waitingPlayers.contains(username);
    }

    public static boolean isCountdownStarted() {
        return countdownStarted;
    }
}