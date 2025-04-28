package server.helpers;

import server.objects.GameConfig;
import server.servants.GameServant;
import java.util.*;
import java.util.concurrent.*;

public class QueueManager {
    private static List<String> waitingPlayers = new ArrayList<>();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> countdownTask;
    private static boolean countdownStarted = false;
    private static int remainingTime = 0;

    public static synchronized void joinQueue(String username) {
        if (!waitingPlayers.contains(username)) {
            waitingPlayers.add(username);
            broadcastQueueUpdate();

            if (!countdownStarted) {
                startCountdown();
            }
        }
    }

    public static synchronized void leaveQueue(String username) {
        if (waitingPlayers.remove(username)) {
            broadcastQueueUpdate();

            if (waitingPlayers.isEmpty() && countdownStarted) {
                cancelCountdown();
            }
        }
    }

    private static void startCountdown() {
        remainingTime = GameConfig.getWaitingTime();
        countdownStarted = true;

        countdownTask = scheduler.scheduleAtFixedRate(() -> {
            remainingTime--;
            broadcastCountdownUpdate();

            if (remainingTime <= 0) {
                startGame();
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
    }

    private static void startGame() {
        if (waitingPlayers.size() >= 2) {
            createNewGame(new ArrayList<>(waitingPlayers));
        } else if (!waitingPlayers.isEmpty()) {
            notifyNoOpponent(waitingPlayers.get(0));
        }

        waitingPlayers.clear();
        countdownStarted = false;
    }

    private static void notifyNoOpponent(String username) {
        // Implement notification logic
        System.out.println("No opponent found for " + username);
    }

    private static void createNewGame(List<String> players) {
        // Implement game creation logic
        System.out.println("Starting game with players: " + players);
    }

    private static void broadcastQueueUpdate() {
        // Notify all waiting players about queue changes
        GameServant.notifyQueueUpdate(waitingPlayers.size());
    }

    private static void broadcastCountdownUpdate() {
        // Notify all waiting players about countdown
        GameServant.notifyCountdownUpdate(remainingTime);
    }

    public static synchronized int getQueueSize() {
        return waitingPlayers.size();
    }

    public static synchronized int getRemainingTime() {
        return remainingTime;
    }

    public static synchronized boolean isInQueue(String username) {
        return waitingPlayers.contains(username);
    }
}