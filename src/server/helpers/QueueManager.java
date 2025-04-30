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
                System.out.println("Countdown finished, starting game");
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
        System.out.println("Countdown canceled");
    }

    private static void startGame() {
        if (waitingPlayers.size() >= 2) {
            System.out.println("Starting game with players: " + waitingPlayers);
            createNewGame(new ArrayList<>(waitingPlayers));
        } else if (!waitingPlayers.isEmpty()) {
            System.out.println("No opponent found for " + waitingPlayers.get(0));
            notifyNoOpponent(waitingPlayers.get(0));
        }

        // Clear callbacks for players no longer in queue
        List<String> currentPlayers = new ArrayList<>(waitingPlayers);
        GameServant.clearCallbacksExcept(currentPlayers);

        waitingPlayers.clear();
        countdownStarted = false;
    }

    private static void notifyNoOpponent(String username) {
        System.out.println("Notifying no opponent for " + username);
        // Task 3
    }

    private static void createNewGame(List<String> players) {
        System.out.println("Creating new game with players: " + players);
        // Task 4
    }

    private static void broadcastQueueUpdate() {
        System.out.println("Broadcasting queue update: " + waitingPlayers.size() + " players");
        GameServant.notifyQueueUpdate(waitingPlayers.size());
    }

    private static void broadcastCountdownUpdate() {
        System.out.println("Broadcasting countdown update: " + remainingTime + " seconds");
        GameServant.notifyCountdownUpdate(remainingTime);
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
}